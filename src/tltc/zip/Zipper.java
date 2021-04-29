package tltc.zip;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class Zipper {
	public static void main(String[] args) {
		Zipper zipper = new Zipper();
		zipper.cli(args);
	}

	public String cli(String[] args) {
//      Arrays.stream(args).forEach(System.out::println);
		String password = null;
		String zipFileName = null;

		// ***Definition Stage***
		// create Options object
		Options options = new Options();
		// add option "-f"
		options.addOption("p", true, "file encryption password");
		options.addOption("f", true, "zip file name");
		options.addOption("h", false, "print help message");

		// ***Parsing Stage***
		// Create a parser
		CommandLineParser parser = new DefaultParser();
		// parse the options passed as command line arguments
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.getMessage();
		}

		// ***Interrogation Stage***
		// hasOptions checks if option is present or not
		if (Objects.requireNonNull(cmd).hasOption("h")) {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(this.getClass().getSimpleName(), options);
		} 
		if (cmd.hasOption("p")) {
			password = cmd.getOptionValue("p");
		} 
		if (cmd.hasOption("f")) {
			zipFileName = cmd.getOptionValue("f"); 
		} else {
			zipFileName = Paths.get("").toAbsolutePath().normalize().getFileName().toString()+".zip";
		}
		if (cmd.getArgs().length == 0) {
			System.err.println("requires files/folders to zip");
			new HelpFormatter().printHelp(this.getClass().getSimpleName(), options);
			System.exit(0);
		}
		this.doZip(zipFileName, password, cmd.getArgs());
		return zipFileName;
    }

	public void doZip(String zipFileName, String password, String[] filesToZip) {
		ZipFile zipFile = null;
		ZipParameters zipParameters = new ZipParameters();
		if (password != null) {
			zipParameters.setEncryptFiles(true);
			zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
//			zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
			zipFile = new ZipFile(zipFileName, password.toCharArray());
		} else {
			zipFile = new ZipFile(zipFileName);
		}

		try {
			for (String file : filesToZip) {
				File f = new File(file);
				if (f.isDirectory())
					zipFile.addFolder(f, zipParameters);
				else 
					zipFile.addFile(f, zipParameters);
			}
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}
    
}
