package SQS_MainMethod;

import SQS_Test.SimpleQueueService_Test;

public class SQS_MainMethod extends SimpleQueueService_Test{
	public static void main(String[] args) throws InterruptedException {
		SimpleQueueService_Test.setUp();
		SimpleQueueService_Test.createQueue();
		SimpleQueueService_Test.redrivePolicy();
		SimpleQueueService_Test.listQueues();
		SimpleQueueService_Test.sendMessage();
		SimpleQueueService_Test.recieveMessage();
		SimpleQueueService_Test.deleteMessage();
		SimpleQueueService_Test.purgeQueue();
		SimpleQueueService_Test.deleteQueue();
		System.out.println("--> Automation Test passed");
	}
}
