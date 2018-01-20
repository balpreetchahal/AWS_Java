package SQS_Test;

import java.util.List;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

public class SimpleQueueService_Test {
	public static AmazonSQSAsyncClient sqs;
	public static String myQueueUrl;
	public static List<Message> messages;

	@SuppressWarnings("deprecation")
	public static void setUp() {
		// Accessing SQS service using ElasticMQ on local host
		BasicAWSCredentials credentials = new BasicAWSCredentials("x", "x");
		sqs = new AmazonSQSAsyncClient(credentials).withEndpoint("http://localhost:9324");
	}

	// Creating a queue
	public static void createQueue() {
		System.out.println("===========================================");
		System.out.println("Creating a new SQS queue called MyQueue.\n");
		CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
		myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
	}

	// RedrivePolicy
	public static void redrivePolicy() {
		System.out.println("===========================================");
		System.out.println("Creating dead letter queue called MyDeadLetterQueue,with redrive policy on source queue.\n");
		CreateQueueRequest deadLetterQueueRequest = new CreateQueueRequest("MyDeadLetterQueue");
		String deadLetterQueue_url = sqs.createQueue(deadLetterQueueRequest).getQueueUrl();
		GetQueueAttributesResult queue_attrs = sqs
				.getQueueAttributes(new GetQueueAttributesRequest(deadLetterQueue_url).withAttributeNames("QueueArn")); //Get dead-letter queue ARN
		String deadLetterQueue_arn = queue_attrs.getAttributes().get("QueueArn");
		SetQueueAttributesRequest request = new SetQueueAttributesRequest().withQueueUrl(myQueueUrl).addAttributesEntry(
				"RedrivePolicy",
				"{\"maxReceiveCount\":\"3\", \"deadLetterTargetArn\":\"" + deadLetterQueue_arn + "\"}");
		sqs.setQueueAttributes(request);
	}

	// List queues
	public static void listQueues() {
		System.out.println("===========================================");
		System.out.println("Listing all queues in your account starting with \"My\".\n");
		for (String queueUrl : sqs.listQueues("My").getQueueUrls()) { //QueueNamePrefix used as parameter of listQueues, to list queues with name starting with-My															
			System.out.println("  QueueUrl: " + queueUrl);
		}
		System.out.println();
	}

	// Sending a message
	public static void sendMessage() {
		System.out.println("===========================================");
		System.out.println("Sending a message to MyQueue.\n");
		sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text.").withDelaySeconds(5)); //Using DelaySeconds to send message in queue after 5 sec 																				
	}

	// Receiving a message
	public static void recieveMessage() {
		System.out.println("===========================================");
		System.out.println("Receiving messages from MyQueue.\n");

		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		receiveMessageRequest.setWaitTimeSeconds(10);         //Set WaitTimeSeconds to 10 seconds for polling for message

		messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message : messages) {
			//Set VisibilityTimeout parameter using ChangeMessageVisibility i.e time until the message will be in queue to be consumed
			sqs.changeMessageVisibility(myQueueUrl, message.getReceiptHandle(), 60);
			System.out.println("  Message");
			System.out.println("    MessageId:     " + message.getMessageId());
			System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
			System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("    Body:          " + message.getBody());
		}
		System.out.println();
	}

	//Deleting a message
	public static void deleteMessage() {
		System.out.println("===========================================");
		System.out.println("Deleting message in MyQueue\n");
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messages.get(0).getReceiptHandle()));
	}

	//Purging a Queue
	public static void purgeQueue() {
		System.out.println("===========================================");
		System.out.println("Purging MyQueue.\n");
		sqs.purgeQueue(new PurgeQueueRequest(myQueueUrl));
	}

	//Deleting a queue
	public static void deleteQueue() {
		System.out.println("===========================================");
		System.out.println("Deleting MyQueue\n");
		sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
	}
}