import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoder;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderClient;
import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.CreateJobResult;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

public class LambdaFunctionHandler implements RequestHandler<S3Event, Object> {
	private static final String ET_PIPELINE_ID = "1453220197148-xt3ow3";
	private static final String ET_PRESET_ID = "1351620000001-000030";

	@Override
	public String handleRequest(S3Event input, Context context) {
		context.getLogger().log("Input: " + input);
		try {
			// get event record
			S3EventNotificationRecord record = input.getRecords().get(0);

			String srcKey = record.getS3().getObject().getKey();

			String dstKey = "transcoded/" + srcKey;

			// ET Client setup
			AmazonElasticTranscoder elasticTranscoder = new AmazonElasticTranscoderClient();
			elasticTranscoder.setRegion(Region.getRegion(Regions.US_WEST_2));
			
			// ET Job Request
			CreateJobRequest createJobRequest=new CreateJobRequest();
			createJobRequest.setPipelineId(ET_PIPELINE_ID);
			
			// ET Job Input
			JobInput jobInput = new JobInput();
			jobInput.setKey(srcKey);
			
			// ET Job Output
			CreateJobOutput createJobOutput = new CreateJobOutput();
			createJobOutput.setPresetId(ET_PRESET_ID);
			createJobOutput.setKey(dstKey);
			
			// Create ET Job
			createJobRequest.setInput(jobInput);
			createJobRequest.setOutput(createJobOutput);
			
			CreateJobResult jobResult=elasticTranscoder.createJob(createJobRequest);
			
			//LOG
			context.getLogger().log("Job Created: " + jobResult.getJob().toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return "OK";
	}

}
