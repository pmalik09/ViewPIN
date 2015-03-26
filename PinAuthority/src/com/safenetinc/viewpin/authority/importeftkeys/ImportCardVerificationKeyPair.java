package com.safenetinc.viewpin.authority.importeftkeys;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class ImportCardVerificationKeyPair 
{
	private static final String APPLICATION_NAME = "ImportCardVerificationKey";
	
	private String zoneMasterKeyLabel = null;
	private String cardVerificationKeyPairLabel = null;
	private String cardVerificationKeyAlpha = null;
	private String cardVerificationKeyBravo = null;
	
	public ImportCardVerificationKeyPair(String[] args) 
	{
		super();
		
		try
		{
			processCommandLine(args);
			
			importCardVerificationKeyPair();
		}
	    catch(ParseException pe)
	    {
	        //No action required
	    }
	}
	
	private void importCardVerificationKeyPair()
	{
		try
		{
			String cardVerificationKeyAlphaKeyLabel = getCardVerificationKeyPairLabel() + "a";
			String cardVerificationKeyBravoKeyLabel = getCardVerificationKeyPairLabel() + "b";

			ImportWorkingKey.unwrapCardVerificationKeyAlpha(getZoneMasterKeyLabel(), cardVerificationKeyAlphaKeyLabel, getCardVerificationKeyAlpha());
			ImportWorkingKey.unwrapCardVerificationKeyBravo(getZoneMasterKeyLabel(), cardVerificationKeyBravoKeyLabel, getCardVerificationKeyBravo());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void processCommandLine(String[] args) throws ParseException
    {
        Option zoneMasterKeyLabelOption;
        Option cardVerificationKeyPairLabelOption;
        Option cardVerificationKeyAlphaOption;
        Option cardVerificationKeyBravoOption;
        Options commandLineOptions;
        CommandLineParser clp;
        CommandLine cl;

        zoneMasterKeyLabelOption = null;
        cardVerificationKeyPairLabelOption = null;
        cardVerificationKeyAlphaOption = null;
        cardVerificationKeyBravoOption = null;
        commandLineOptions = null;
        clp = null;
        cl = null;

        zoneMasterKeyLabelOption = new Option("z", "zmklabel", true, "zone master key label");
        zoneMasterKeyLabelOption.setRequired(true);
        zoneMasterKeyLabelOption.setArgs(1);
        zoneMasterKeyLabelOption.setOptionalArg(false);
        zoneMasterKeyLabelOption.setArgName("zmklabel");
        
        cardVerificationKeyPairLabelOption = new Option("c", "cvklabel", true, "card verification key pair label");
        cardVerificationKeyPairLabelOption.setRequired(true);
        cardVerificationKeyPairLabelOption.setArgs(1);
        cardVerificationKeyPairLabelOption.setOptionalArg(false);
        cardVerificationKeyPairLabelOption.setArgName("cvklabel");
        
        cardVerificationKeyAlphaOption = new Option("a", "cvka", true, "encoded encrypted card verification key A");
        cardVerificationKeyAlphaOption.setRequired(true);
        cardVerificationKeyAlphaOption.setArgs(1);
        cardVerificationKeyAlphaOption.setOptionalArg(false);
        cardVerificationKeyAlphaOption.setArgName("cvka");
        
        cardVerificationKeyBravoOption = new Option("b", "cvkb", true, "encoded encrypted card verification key B");
        cardVerificationKeyBravoOption.setRequired(true);
        cardVerificationKeyBravoOption.setArgs(1);
        cardVerificationKeyBravoOption.setOptionalArg(false);
        cardVerificationKeyBravoOption.setArgName("cvkb");

        commandLineOptions = new Options();
        commandLineOptions.addOption(zoneMasterKeyLabelOption);
        commandLineOptions.addOption(cardVerificationKeyPairLabelOption);
        commandLineOptions.addOption(cardVerificationKeyAlphaOption);
        commandLineOptions.addOption(cardVerificationKeyBravoOption);

        clp = new PosixParser();

        try
        {
            cl = clp.parse(commandLineOptions, args);
            
            setZoneMasterKeyLabel(cl.getOptionValue('z'));
            setCardVerificationKeyPairLabel(cl.getOptionValue('c')); 
            setCardVerificationKeyAlpha(cl.getOptionValue('a'));
            setCardVerificationKeyBravo(cl.getOptionValue('b'));
        }
        catch (ParseException pe)
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
	
	private void setCardVerificationKeyPairLabel(String cardVerificationKeyPairLabel) 
	{
		this.cardVerificationKeyPairLabel = cardVerificationKeyPairLabel;
	}
	
	private String getCardVerificationKeyPairLabel() 
	{
		return this.cardVerificationKeyPairLabel;
	}

	private void setCardVerificationKeyAlpha(String cardVerificationKeyAlpha) 
	{
		this.cardVerificationKeyAlpha = cardVerificationKeyAlpha;
	}
	
	private String getCardVerificationKeyAlpha() 
	{
		return this.cardVerificationKeyAlpha;
	}

	private void setCardVerificationKeyBravo(String cardVerificationKeyBravo) 
	{
		this.cardVerificationKeyBravo = cardVerificationKeyBravo;
	}
	
	private String getCardVerificationKeyBravo() 
	{
		return this.cardVerificationKeyBravo;
	}

	public static void main(String[] args) 
	{
		new ImportCardVerificationKeyPair(args);
	}
}
