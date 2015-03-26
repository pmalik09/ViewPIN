package com.safenetinc.viewpin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * The {@link ProtectedAreaFileManager} is responsible for moving files between the Luna SP's /usr-files
 * directory and the /usr-xfiles directory
 * 
 * @author Stuart Horler
 */
public class ProtectedAreaFileManager
{
    private static final String IMPORT_FILE_AREA    = "/usr-files";

    private static final String PROTECTED_FILE_AREA = "/usr-xfiles";

    /**
     * Takes an array of {@link String} objects intended to be the command line arguments passed to the
     * application. Uses commons-cli to parse these arguments.
     * 
     * @param args
     */
    public ProtectedAreaFileManager(String[] args)
    {
        super();

        try 
        {
			processCommandLine(args);
		} 
        catch(ParseException pe) 
        {
            System.err.println(pe.getMessage());
		}
    }

    private void processCommandLine(String[] args) throws ParseException
    {
        Option importOption;
        Option exportOption;
        Option listOption;
        Option deleteOption;
        OptionGroup mutuallyExclusiveOptions;
        Options commandLineOptions;
        CommandLineParser clp;
        CommandLine cl;
        Option[] parsedOptions;
        File importFile;
        File exportFile;
        File deleteFile;

        importOption = null;
        exportOption = null;
        listOption = null;
        deleteOption = null;
        mutuallyExclusiveOptions = null;
        commandLineOptions = null;
        clp = null;
        cl = null;

        importFile = null;
        exportFile = null;
        deleteFile = null;

        importOption = new Option("i", "import", true, "import file into protected area");
        exportOption = new Option("e", "export", true, "export file from protected area");
        listOption = new Option("l", "list", false, "list files in protected area");
        deleteOption = new Option("d", "delete", false, "delete file from protected area");

        importOption.setArgs(1);
        importOption.setOptionalArg(false);
        importOption.setArgName("file");

        exportOption.setArgs(1);
        exportOption.setOptionalArg(false);
        exportOption.setArgName("file");

        deleteOption.setArgs(1);
        deleteOption.setOptionalArg(false);
        deleteOption.setArgName("file");

        // Define import and export options as mutually exclusive
        mutuallyExclusiveOptions = new OptionGroup();
        mutuallyExclusiveOptions.addOption(importOption);
        mutuallyExclusiveOptions.addOption(exportOption);
        mutuallyExclusiveOptions.addOption(listOption);
        mutuallyExclusiveOptions.addOption(deleteOption);
        mutuallyExclusiveOptions.setRequired(true);

        commandLineOptions = new Options();
        commandLineOptions.addOptionGroup(mutuallyExclusiveOptions);

        clp = new PosixParser();

        try
        {
            cl = clp.parse(commandLineOptions, args);

            parsedOptions = cl.getOptions();

            // Ensure import option is not duplicated
            if (isOptionDuplicated(parsedOptions, importOption.getId()) == true)
            {
                throw new ParseException("import option duplicated");
            }

            // Ensure import option is not duplicated
            if (isOptionDuplicated(parsedOptions, importOption.getId()) == true)
            {
                throw new ParseException("export option duplicated");
            }

            // Ensure list option is not duplicated
            if (isOptionDuplicated(parsedOptions, listOption.getId()) == true)
            {
                throw new ParseException("list option duplicated");
            }

            // Ensure delete option is not duplicated
            if (isOptionDuplicated(parsedOptions, deleteOption.getId()) == true)
            {
                throw new ParseException("delete option duplicated");
            }

            if (cl.hasOption('i') == true)
            {
                importFile = new File(cl.getOptionValue('i'));

                try
                {
                    importFile(importFile);

                    System.out.println("imported " + importFile.getName() + " OK");
                }
                catch (FileNotFoundException fnfe)
                {
                    throw new ParseException("import file " + importFile.getName() + " not found");
                }
                catch (IOException ioe)
                {
                    throw new ParseException("failed to copy import file " + ioe.getMessage());
                }
            }
            else
            {
                if (cl.hasOption('e') == true)
                {
                    exportFile = new File(cl.getOptionValue('e'));

                    try
                    {
                        exportFile(exportFile);

                        System.out.println("exported " + exportFile.getName() + " OK");
                    }
                    catch (FileNotFoundException fnfe)
                    {
                        throw new ParseException("export file " + exportFile.getName() + " not found");
                    }
                    catch (IOException ioe)
                    {
                        throw new ParseException("failed to copy import file");
                    }
                }
                else
                {
                    if (cl.hasOption('l') == true)
                    {
                        listFiles(new File(PROTECTED_FILE_AREA));
                    }
                    else
                    {
                        if (cl.hasOption('d') == true)
                        {
                            deleteFile = new File(cl.getOptionValue('d'));

                            deleteFile(deleteFile);
                        }
                        else
                        {
                            throw new ParseException("");
                        }
                    }
                }
            }
        }
        catch(ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(this.getClass().getName(), commandLineOptions, true);
          
            throw pe;
        }
    }

    private void listFiles (File directory)
    {
        File[] files;

        files = directory.listFiles();

        if (files == null)
        {
            System.out.println("protected area is empty");

            return;
        }

        for (int i = 0; i < files.length; i++)
        {
            System.out.println(files[i].getName());
        }
    }

    private void importFile (File file) throws FileNotFoundException, IOException
    {
        String sourceDirectory;
        File sourceFile;
        String destinationDirectory;
        File destinationFile;

        sourceDirectory = null;
        sourceFile = null;
        destinationDirectory = null;
        destinationFile = null;

        sourceDirectory = IMPORT_FILE_AREA;
        sourceFile = new File(sourceDirectory + "/" + file.getName());

        destinationDirectory = PROTECTED_FILE_AREA;
        destinationFile = new File(destinationDirectory + "/" + file.getName());

        copyFile(sourceFile, destinationFile);

        // Delete the original
        boolean success = sourceFile.delete();
        if(!success)
        {
            //Nothing to do here
            success = true;
        }
    }

    private void exportFile (File file) throws FileNotFoundException, IOException
    {
        String sourceDirectory;
        File sourceFile;
        String destinationDirectory;
        File destinationFile;

        sourceDirectory = null;
        sourceFile = null;
        destinationDirectory = null;
        destinationFile = null;

        sourceDirectory = PROTECTED_FILE_AREA;
        sourceFile = new File(sourceDirectory + "/" + file.getName());

        destinationDirectory = IMPORT_FILE_AREA;
        destinationFile = new File(destinationDirectory + "/" + file.getName());

        copyFile(sourceFile, destinationFile);
    }

    private void copyFile (File source, File destination) throws IOException
    {
        FileInputStream fisSource;
        FileOutputStream fosDestination;
        int nextByte;

        fisSource = null;
        fosDestination = null;
        nextByte = 0;

        try
        {
            fisSource = new FileInputStream(source);
            fosDestination = new FileOutputStream(destination);

            while ((nextByte = fisSource.read()) != -1)
            {
                fosDestination.write(nextByte);
            }
        }
        catch (IOException ioe)
        {
            throw ioe;
        }
        finally
        {
            try
            {
                if (fisSource != null)
                    fisSource.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
                if (fosDestination != null)
                    fosDestination.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void deleteFile (File file)
    {
        File f;

        f = null;

        // Build full pathname to the file in protected area we wish to delete
        f = new File(PROTECTED_FILE_AREA + "/" + file.getName());

        // Ensure file exists before we attempt to delete it
        if (f.exists() == false)
        {
            // File does not exist
            System.out.println(file.getName() + " does not exist");

            return;
        }

        // Ensure file is normal before we attempt to delete it
        if (f.isFile() == false)
        {
            // File is not normal
            System.out.println(file.getName() + " not normal file");

            return;
        }

        // Delete file
        if (f.delete() == false)
        {
            // Failed to delete file
            System.out.println("failed to delete " + file.getName());

            return;
        }

        // Deleted file OK
        System.out.println("deleted " + file.getName() + " OK");
    }

    private boolean isOptionDuplicated (Option[] parsedOptions, int optionLetter)
    {
        boolean optionDuplicated;

        optionDuplicated = false;

        if (getTotalParsedOptions(parsedOptions, optionLetter) > 1)
        {
            optionDuplicated = true;
        }

        return optionDuplicated;
    }

    private int getTotalParsedOptions (Option[] parsedOptions, int optionLetter)
    {
        int totalParsedOptions;
        Option nextParsedOption;

        totalParsedOptions = 0;
        nextParsedOption = null;

        for (int i = 0; i < parsedOptions.length; i++)
        {
            nextParsedOption = parsedOptions[i];

            if (nextParsedOption.getId() == optionLetter)
            {
                totalParsedOptions++;
            }
        }

        return totalParsedOptions;
    }

    /**
     * Main method for the application. Simply constructs a new {@link ProtectedAreaFileManager} object
     * passing the command line arguments.
     * 
     * @param args Standard command line arguments
     */
    public static void main (String[] args)
    {
		try
		{
		  new ProtectedAreaFileManager(args);
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("Illegal arguemnts passed");
			System.out.println("ProtectedAreaFileManager failed");
			return;
		}
		catch(NegativeArraySizeException e)
		{
			System.out.println("Arguments invalid");
			System.out.println("ProtectedAreaFileManager failed");
			return;
		}
		catch(ClassCastException e)
		{
			System.out.println("Could not execute ProtectedAreaFileManager");
			System.out.println("ProtectedAreaFileManager failed");
			return;
		}
		catch(NullPointerException e)
		{
			System.out.println("Passed values null not accepted");
			System.out.println("ProtectedAreaFileManager failed");
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Check the arguments passed");
			System.out.println("ProtectedAreaFileManager failed");
			return;
		}

    
    }
}