package com.safenetinc.viewpin.cli;

import javax.xml.xpath.XPathConstants;
import java.lang.*;

/* 
 *	class to hold the PinAuthority Configuration element
*/
public class PinAuthorityConfigurationElements {
	
	private static String PinAuthorityWrappingCertificateSKIKey = null;
	private static String PinAuthoritySigningKeySKI = null;
	
	private  static String MaxFailedAttempts = null;
	private  static String MaximumReplayOpportunityWindow = null;
	private  static String TrustStoreLocation = null;
	
	private  static String CardHolderCVVEncryption = null;
	private  static String CardHolderExpiryDateEncryption = null;
	private  static String CardHolderPANElementEncryption = null;
	private  static String CardHolderPINElementEncryption = null;
	
	private  static String CardHolderCVV = null;
	private  static String CardHolderExpiryDate = null;
	private  static String CardHolderPAN = null;
	private  static String CardHolderPIN = null;
	
	private  static String CardHolderCVVKeyIdentifier = null;
	private  static String CardHolderExpiryDateKeyIdentifier = null;
	private  static String CardHolderPANKeyIdentifier = null;
	private  static String CardHolderPINKeyIdentifier = null;
	
	private  static String CardHolderCVVKeyType = null;
	private  static String CardHolderExpiryDateKeyType = null;
	private  static String CardHolderPANKeyType = null;
	private  static String CardHolderPINKeyType = null;
	
	private  static String CardHolderCVVEncryptionTransform = null;
	private  static String CardHolderExpiryDateEncryptionTransform = null;
	private  static String CardHolderPANEncryptionTransform = null;
	private  static String CardHolderPINEncryptionTransform = null;
	
	public PinAuthorityConfigurationElements()
	{
	}
	
	public void setCardHolderCVVEncryption(String CardHolderCVVEncryption){ 
		this.CardHolderCVVEncryption = CardHolderCVVEncryption;
	}
	
	public void setCardHolderExpiryDateEncryption(String CardHolderExpiryDateEncryption){
		this.CardHolderExpiryDateEncryption = CardHolderExpiryDateEncryption;
	}
	
	public void setCardHolderPANEncryption(String CardHolderPANElementEncryption){
		this.CardHolderPANElementEncryption = CardHolderPANElementEncryption;
	}
	
	public  void setCardHolderPINEncryption(String CardHolderPINElementEncryption){
		this.CardHolderPINElementEncryption = CardHolderPINElementEncryption;
	}
	
	/**
	  * @return The CVV Encryption value for the card
	  */
	public  String getCardHolderCVVEncryption(){
		return this.CardHolderCVVEncryption;
	}
	
	/**
	  * @return The ExpiryDate Encryption value for the card
	  */
	public  String getCardHolderExpiryDateEncryption(){
		return this.CardHolderExpiryDateEncryption;
	}
	
	/**
	  * @return The PAN Encryption value for the card
	  */
	public  String getCardHolderPANEncryption(){
		return this.CardHolderPANElementEncryption;
	}
	
	/**
	  * @return The PIN Encryption value for the card
	  */
	public  String getCardHolderPINEncryption(){
		return this.CardHolderPINElementEncryption;
	}
		
	
	public  void setCardHolderCVV(String CardHolderCVV){ 
		this.CardHolderCVV = CardHolderCVV;
	}

	public  void setCardHolderExpiryDate(String CardHolderExpiryDate){
		this.CardHolderExpiryDate = CardHolderExpiryDate;
	}
	
	public  void setCardHolderPAN(String CardHolderPAN){
		this.CardHolderPAN = CardHolderPAN;
	}
	
	public  void setCardHolderPIN(String CardHolderPIN){
		this.CardHolderPIN = CardHolderPIN;
	}
	/**
	  * @return The CVV value for the card
	  */
	public String getCardHolderCVV(){
		return this.CardHolderCVV;
	}
	/**
	  * @return The ExpiryDate value for the card
	  */
	public  String getCardHolderExpiryDate(){
		return this.CardHolderExpiryDate;
	}
	/**
	  * @return The PAN Encryption value for the card
	  */
	public  String getCardHolderPAN(){
		return this.CardHolderPAN;
	}
	/**
	  * @return The PIN value for the card
	  */
	public  String getCardHolderPIN(){
		return this.CardHolderPIN;
	}
	
	public  void setCardHolderCVVKeyIdentifier(String CardHolderCVVKeyIdentifier){ 
		this.CardHolderCVVKeyIdentifier = CardHolderCVVKeyIdentifier;
	}
	
	public  void setCardHolderExpiryDateKeyIdentifier(String CardHolderExpiryDateKeyIdentifier){
		this.CardHolderExpiryDateKeyIdentifier = CardHolderExpiryDateKeyIdentifier;
	}
	
	public  void setCardHolderPANKeyIdentifier(String CardHolderPANKeyIdentifier){
		this.CardHolderPANKeyIdentifier = CardHolderPANKeyIdentifier;
	}
	
	public  void setCardHolderPINKeyIdentifier(String CardHolderPINKeyIdentifier){
		this.CardHolderPINKeyIdentifier = CardHolderPINKeyIdentifier;
	}
	/**
	  * @return The CVV key Identifier value for the card
	  */
	public String getCardHolderCVVKeyIdentifier(){
		return this.CardHolderCVVKeyIdentifier;
	}
	/**
	  * @return The ExpiryDate key Identifier value for the card
	  */
	public  String getCardHolderExpiryDateKeyIdentifier(){
		return this.CardHolderExpiryDateKeyIdentifier;
	}
	/**
	  * @return The PAN key Identifier value for the card
	  */
	public  String getCardHolderPANKeyIdentifier(){
		return this.CardHolderPANKeyIdentifier;
	}
	/**
	  * @return The PIN key Identifier value for the card
	  */
	public  String getCardHolderPINKeyIdentifier(){
		return this.CardHolderPINKeyIdentifier;
	}
	
	public  void setCardHolderCVVKeyType(String CardHolderCVVKeyType){ 
		this.CardHolderCVVKeyType = CardHolderCVVKeyType;
	}
	
	public  void setCardHolderExpiryDateKeyType(String CardHolderExpiryDateKeyType){
		this.CardHolderExpiryDateKeyType = CardHolderExpiryDateKeyType;
	}
	
	public  void setCardHolderPANKeyType(String CardHolderPANKeyType){
		this.CardHolderPANKeyType = CardHolderPANKeyType;
	}
	
	public  void setCardHolderPINKeyType(String CardHolderPINKeyType){
		this.CardHolderPINKeyType = CardHolderPINKeyType;
	}
	/**
	  * @return The CVV key type value for the card
	  */
	public String getCardHolderCVVKeyType(){
		return this.CardHolderCVVKeyType;
	}
	/**
	  * @return The ExpiryDate  key type value for the card
	  */
	public  String getCardHolderExpiryDateKeyType(){
		return this.CardHolderExpiryDateKeyType;
	}
	/**
	  * @return The PAN key type value for the card
	  */
	public  String getCardHolderPANKeyType(){
		return this.CardHolderPANKeyType;
	}
	/**
	  * @return The PIN key type value for the card
	  */
	public  String getCardHolderPINKeyType(){
		return this.CardHolderPINKeyType;
	}
	
	public  void setCardHolderCVVEncryptionTransformation(String CardHolderCVVTransformation){ 
		this.CardHolderCVVEncryptionTransform = CardHolderCVVTransformation;
	}
	
	public  void setCardHolderExpiryDateEncryptionTransformation(String CardHolderExpiryDateTransformation){
		this.CardHolderExpiryDateEncryptionTransform = CardHolderExpiryDateTransformation;
	}
	
	public  void setCardHolderPANEncryptionTransformation(String CardHolderPANTransformation){
		this.CardHolderPANEncryptionTransform = CardHolderPANTransformation;
	}
	
	public  void setCardHolderPINEncryptionTransformation(String CardHolderPINTransformation){
		this.CardHolderPINEncryptionTransform = CardHolderPINTransformation;
	}
	/**
	  * @return The CVV encryption Transformation
	  */
	public String getCardHolderCVVEncryptionTransformation(){
		return this.CardHolderCVVEncryptionTransform;
	}
	/**
	  * @return The ExpiryDate encryption Transformation
	  */
	public  String getCardHolderExpiryDateEncryptionTransformation(){
		return this.CardHolderExpiryDateEncryptionTransform;
	}
	/**
	  * @return The PAN encryption Transformation
	  */
	public  String getCardHolderPANEncryptionTransformation(){
		return this.CardHolderPANEncryptionTransform;
	}
	/**
	  * @return The PIN encryption Transformation
	  */
	public  String getCardHolderPINEncryptionTransformation(){
		return this.CardHolderPINEncryptionTransform;
	}
			
	public  void setMaximumReplayOpportunityWindow(String MaximumReplayOpportunityWindow)
	{
		this.MaximumReplayOpportunityWindow = MaximumReplayOpportunityWindow;
	}
	/**
	  * @return The Maximum Failed Attempts counter
	  */
	public String getMaxFailedAttempts()
	{
		return this.MaxFailedAttempts;
	}
	
	public  void setMaxFailedAttempts(String MaxFailedAttempts)
	{
		this.MaxFailedAttempts = MaxFailedAttempts;
	}
	/**
	  * @return The Maximum Replay Window
	  */
	public String getMaximumReplayOpportunityWindow()
	{
		return this.MaximumReplayOpportunityWindow;
	}
	
	public  void setPinAuthoritySigningKeySKI(String PinAuthoritySigningKeySKI)
	{
		this.PinAuthoritySigningKeySKI = PinAuthoritySigningKeySKI;
	}
	/**
	  * @return The Siginig Key
	  */
	public String getPinAuthoritySigningKeySKI()
	{
		return this.PinAuthoritySigningKeySKI;
	}
	
	public  void setPinAuthorityWrappingCertificateSKIKey(String PinAuthorityWrappingCertificateSKIKey)
	{
		this.PinAuthorityWrappingCertificateSKIKey = PinAuthorityWrappingCertificateSKIKey;
			
	}
	/**
	  * @return The Wrapping Certificate SKI Key
	  */
	public String getPinAuthorityWrappingCertificateSKIKey()
	{
		return this.PinAuthorityWrappingCertificateSKIKey;
	}
	
}