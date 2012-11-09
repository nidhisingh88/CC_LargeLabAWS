package main.java.nl.tud.cc.rest.resources;

import java.io.File;
import java.io.FileOutputStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.multipart.FormDataParam;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

@Path("/converter")
public class ConverterResource {

	@Context ServletContext sc;
	@POST
	@Path("/video")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	/**
	 * 
	 * @param inputStream
	 * @param inputFileName
	 * @param outputFormat
	 * @return
	 */
	public Response Converter(@FormDataParam("inputFileStream") InputStream inputStream,
			                   @FormDataParam("inputFileName") String inputFileName,
			                   @FormDataParam("outputFormat") String outputFormat)
	{
		
		//locate the temp dir for storing input video file
		File tempDir = (File) sc.getAttribute("javax.servlet.context.tempdir");
		String tempPath = tempDir.getAbsolutePath();
		
		//create File for input
		File inputFile = new File(tempPath + "/" + inputFileName);
		
	    //check whether file already exists if so add a number to it to make the file unique
		determineUniqueFilePath(inputFile);
		
		try {
			inputFile.createNewFile();
		} catch (IOException e) {
			return ErrorResponse("IOException: Failed to create input file.");
		}
		
		//load data in input file
		try {
			saveToFile(inputStream,inputFile);
		} catch (Exception e) {
			return ErrorResponse("InputStream cannot be saved to file.");
		}
		
		//determine where the output file will be stored
		String outputLoc = inputFile.getAbsolutePath() + outputFormat;
		
		convertVideo(inputFile.getPath(),outputLoc);
		
		//create stream for downloading output file
		FileInputStream fis = null;
		try {
	
			fis = new FileInputStream(outputLoc);
			// delete all residual files
			inputFile.delete();
			File outputFile = new File(outputLoc);
			outputFile.delete();

		} catch (FileNotFoundException e) {
			inputFile.delete();
			return ErrorResponse("FileNotFoundException: Failed to read from output file");
		}
		return Response.ok().entity(fis).build();
		
	}
	
	/**
	 * Stores an inputstream in a file.
	 * @param InputStream inputStream
	 * @param File f
	 * @throws Exception
	 */
	protected void saveToFile(InputStream inputStream,
		File f) throws Exception {

			OutputStream out = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(f);
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();


	}
	
	/**
	 * Converts video located in iPath to a video that will be written in oPath.
	 * 
	 * @param iPath path of input file.
	 * @param oPath path of output file.
	 */
	protected void convertVideo(String iPath,String oPath){
		 
		
		IMediaReader reader = ToolFactory.makeReader(iPath);
		 reader.addListener(ToolFactory.makeWriter(oPath, reader));
		 while (reader.readPacket() == null);
	}
	
	protected Response ErrorResponse(String msg) {
		return Response.serverError().entity(msg).build();
	}
	
	protected void determineUniqueFilePath(File file){
		
		String defaultPath = file.getAbsolutePath();
		for(int i=0; i <= Integer.MAX_VALUE; i++){
			if(file.exists()){
				file.renameTo(new File(defaultPath + "(" + i + ")"));
			}
			else{
				break;
			}
		}
	}

	
}
