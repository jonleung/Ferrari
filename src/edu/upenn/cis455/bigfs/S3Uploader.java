//package edu.upenn.cis455.bigfs;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//
//import com.amazonaws.AmazonClientException;
//import com.amazonaws.AmazonServiceException;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.Bucket;
//import com.amazonaws.services.s3.transfer.TransferManager;
//import com.amazonaws.services.s3.transfer.Upload;
//import com.amazonaws.util.StringUtils;
//
//// http://ceph.com/docs/master/radosgw/s3/java/
//// http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/transfer/package-summary.html
//
//class S3Uploader extends Worker{
//
//	String s3BucketName;
//	BlockingQueue<BigFile> bigFileQueue;
//
//	AmazonS3 s3Conn;
//	Bucket urlBucket;
//	Bucket docBucket;
//
//	public void upload(BigFile bigFile) {
//		bigFileQueue.offer(bigFile);
//	}
//
//	public S3Uploader(String s3BucketName) {
//		super();
//
//		this.s3BucketName = s3BucketName;
//		bigFileQueue = new LinkedBlockingQueue<BigFile>();
//
//		String awsAccessKey = "AKIAIJ2QBA5EOFX7HPTQ";
//		String awsSecretKey = "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";
//
//		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
//		this.s3Conn = new AmazonS3Client(credentials);
//
//		this.urlBucket = s3Conn.createBucket(getUrlBucketName());
//		this.docBucket = s3Conn.createBucket(getDocBucketName());
//
//		this.start();
//	}
//
//	@Override
//	protected void work() {
//		BigFile bigFile = null;
//		try {
//			bigFile = bigFileQueue.take();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		String typedBucketName = getTypeBucketName(bigFile);
////
////		TransferManager tx = new TransferManager(this.s3Conn);
////		Upload bigFileUpload = tx.upload(typedBucketName, bigFile.getFilename(), bigFile.getFile());
////
////		while (!bigFileUpload.isDone()) {
////			System.out.println("Transfer: " + bigFileUpload.getDescription());
////			System.out.println("  - State: " + bigFileUpload.getState());
////			System.out.println("  - Progress: " + bigFileUpload.getProgress().getBytesTransfered());
////			try {
////				Thread.sleep(2000);
////			} catch (InterruptedException e) {
////				e.printStackTrace();
////			}
////		}
////
////		try {
////			bigFileUpload.waitForCompletion();
////		} catch (AmazonServiceException e) {
////			e.printStackTrace();
////		} catch (AmazonClientException e) {
////			e.printStackTrace();
////		} catch (InterruptedException e) {
////			e.printStackTrace();
////		}
////
//		System.out.println(String.format("Uploaded %s to bucket %s on S3!", bigFile.getFilename(), typedBucketName));
//	}
//
//	public static void main(String[] args) throws IOException, InterruptedException {
//		// Standalone
//
//		File temp = File.createTempFile("tempfile", ".tmp"); 
//
//		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
//		bw.write("This is the temporary file content");
//		bw.close();
//
//		String awsAccessKey = "AKIAIJ2QBA5EOFX7HPTQ";
//		String awsSecretKey = "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";
//
//		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
//		AmazonS3 s3Conn = new AmazonS3Client(credentials);
//
//		List<Bucket> buckets = s3Conn.listBuckets();
//		for (Bucket bucket : buckets) {
//			System.out.println(bucket.getName() + "\t" +
//					StringUtils.fromDate(bucket.getCreationDate()));
//		}
//
//		TransferManager tx = new TransferManager(credentials);
//		Upload myUpload = tx.upload("BigFs", Long.toString(System.currentTimeMillis()), temp);
//
//
//		// You can poll your transfer's status to check its progress
//		while (myUpload.isDone() == false) {
//			System.out.println("Transfer: " + myUpload.getDescription());
//			System.out.println("  - State: " + myUpload.getState());
//			System.out.println("  - Progress: " + myUpload.getProgress().getBytesTransfered());
//			Thread.currentThread().sleep(1000);
//		}
//
//		System.out.println("Successfully created connection"); 
//	}
//
//	public String getUrlBucketName() {
//		return "bigurlfiles";
//	}
//
//	public String getDocBucketName() {
//		return "bigdocfiles";
//	}
//
//
//	public String getTypeBucketName(BigFile bigFile) {
//		if (bigFile.type == "url") {
//			return getUrlBucketName();
//		}
//		else if (bigFile.type == "doc") {
//			return getDocBucketName();
//		}
//
//		throw new RuntimeException("Invalid Type");
//	}
//
//}
