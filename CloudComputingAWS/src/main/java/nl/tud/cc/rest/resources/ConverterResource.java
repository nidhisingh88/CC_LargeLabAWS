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

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

@Path("/converter")
public class ConverterResource {
	
	@Context ServletContext sc;
	@POST
	@Path("/video")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("fileToUpload[]") InputStream uploadedInputStream,
			                   @FormDataParam("fileToUpload[]") FormDataContentDisposition fileDetail
			                   )
	{
		
		//locate the temp dir for storing input video file
		File tempDir = (File) sc.getAttribute("javax.servlet.context.tempdir");
		
		String tempPath = sc.getRealPath(tempDir.getPath());

		//create File for input
		File inputFile = new File(tempPath + "\\" + fileDetail.getFileName());
		
		try {
			inputFile.createNewFile();
		} catch (IOException e) {
			inputFile.delete();
			return Response.serverError().build();
		}
		
		//load data in input file
		saveToFile(uploadedInputStream,inputFile);
		
		//determine where the output file will be stored
		String outputLoc = tempPath + "\\output" + ".mov";
		
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
			return Response.serverError().build();
		}
		return Response.ok().entity(fis).build();
		
	}
	
	// save uploaded file to new location
	private void saveToFile(InputStream uploadedInputStream,
		File f) {

		try {
			OutputStream out = null;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(f);
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	/**
	 * Converts video located in iPath to a video that will be written in oPath.
	 * 
	 * @param iPath path of input file.
	 * @param oPath path of output file.
	 */
	private void convertVideo(String iPath,String oPath){
		 
		
		IMediaReader reader = ToolFactory.makeReader(iPath);
		 reader.addListener(ToolFactory.makeWriter(oPath, reader));
		 while (reader.readPacket() == null);
	}
	

	
}
