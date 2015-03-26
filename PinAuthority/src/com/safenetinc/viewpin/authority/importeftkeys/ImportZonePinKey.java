package com.safenetinc.viewpin.authority.importeftkeys;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ImportZonePinKey 
{
	private static final String APPLICATION_NAME = "ImportZonePinKey";
	
	private String zoneMasterKeyLabel = null;
	private String zonePinKeyLabel = null;
	private String zonePinKey = null;
	
	public ImportZonePinKey(String[] args)
	{
		super();
		
		try
		{
			processCommandLine(args);
			
			importZonePinKey();
		}
	    catch(ParseException pe)
	    {
	        //No action required
	    }
	}
	
	private void importZonePinKey()
	{
		try
		{
			ImportWorkingKey.unwrapZonePinkey(getZoneMasterKeyLabel(), getZonePinKeyLabel(), getZonePinKey());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void processCommandLine(String[] args) throws ParseException
    {
        Option zoneMasterKeyLabelOption;
        Option zonePinKeyLabelOption;
        Option zonePinKeyOption;
        Options commandLineOptions;
        CommandLineParser clp;
        CommandLine cl;
        
        zoneMasterKeyLabelOption = null;
        zonePinKeyLabelOption = null;
        zonePinKeyOption = null;
        commandLineOptions = null;
        clp = null;
        cl = null;
        
        zoneMasterKeyLabelOption = new Option("z", "zmklabel", true, "zone master key label");
        zoneMasterKeyLabelOption.setRequired(true);
        zoneMasterKeyLabelOption.setArgs(1);
        zoneMasterKeyLabelOption.setOptionalArg(false);
        zoneMasterKeyLabelOption.setArgName("zmklabel");
        
        zonePinKeyLabelOption = new Option("p", "zpklabel", true, "zone pin key label");
        zonePinKeyLabelOption.setRequired(true);
        zonePinKeyLabelOption.setArgs(1);
        zonePinKeyLabelOption.setOptionalArg(false);
        zonePinKeyLabelOption.setArgName("zpklabel");
        
        zonePinKeyOption = new Option("e", "zpk", true, "encoded encrypted zone pin key");
        zonePinKeyOption.setRequired(true);
        zonePinKeyOption.setArgs(1);
        zonePinKeyOption.setOptionalArg(false);
        zonePinKeyOption.setArgName("zpk");
        
        commandLineOptions = new Options();
        commandLineOptions.addOption(zoneMasterKeyLabelOption);
        commandLineOptions.addOption(zonePinKeyLabelOption);
        commandLineOptions.addOption(zonePinKeyOption);
        
        clp = new PosixParser();

        try
        {
            cl = clp.parse(commandLineOptions, args);
            
            setZoneMasterKeyLabel(cl.getOptionValue('z'));
            setZonePinKeyLabel(cl.getOptionValue('p'));
            setZonePinKey(cl.getOptionValue('e'));
        }
        catch(ParseException pe)
        {
            HelpFormatter formatter = new HelpFormatter();

            formatter.printHelp(APPLICATION_NAME, commandLineOptions, true);

            throw pe;
        }
    }
	
	private void setZoneMasterKeyLabel(String zoneMasterKeyLabel) 
	{
		this.zoneMasterKeyLabel = zoneMasterKeyLabel;
	}
	
	private String getZoneMasterKeyLabel() 
	{
		return this.zoneMasterKeyLabel;
	}
	
	private void setZonePinKeyLabel(String zonePinKeyLabel) 
	{
		this.zonePinKeyLabel = zonePinKeyLabel;
	}
	
	private String getZonePinKeyLabel() 
	{
		return this.zonePinKeyLabel;
	}

	private void setZonePinKey(String zonePinKey) 
	{
		this.zonePinKey = zonePinKey;
	}
	
	private String getZonePinKey() 
	{
		return this.zonePinKey;
	}

	public static void main(String[] args) 
	{
		new ImportZonePinKey(args);
	}
}
