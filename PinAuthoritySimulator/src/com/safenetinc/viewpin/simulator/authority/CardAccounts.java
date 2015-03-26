// $Id: PinAuthoritySimulator/src/com/safenetinc/viewpin/simulator/authority/CardAccounts.java 1.1 2008/09/04 10:48:53IST Mkhurana Exp  $
package com.safenetinc.viewpin.simulator.authority;

import java.util.ArrayList;

import com.safenetinc.viewpin.simulator.authority.exceptions.DuplicateCardAccountException;

/**
 * 
 * Class to hold a list of {@link CardAccount} objects for use by the BankSimulator
 * 
 * @author Stuart Horler
 * 
 * 
 */
public class CardAccounts
{
    private ArrayList<CardAccount> cardAccounts = null;

    /**
     * Creates and empty list of card accounts
     */
    public CardAccounts()
    {
        super();

        setCardAccounts(new ArrayList<CardAccount>());
    }

    /**
     * Adds a {@link CardAccount} to the list
     * 
     * @param cardAccount The card account to add
     * @throws DuplicateCardAccountException Thrown if a matching card account already exists
     */
    public synchronized void addCardAccount (CardAccount cardAccount) throws DuplicateCardAccountException
    {
        // Ensure card account with the same primary account number does not already exist
        if (getCardAccount(cardAccount.getPrimaryAccountNumber()) == null)
        {
            // Card account with the same primary account number does not already exist, store it
            getCardAccounts().add(cardAccount);
        }
        else
        {
            // Card account with the same primary account number already exists
            throw new DuplicateCardAccountException();
        }
    }

    /**
     * Gets a card account from the list
     * 
     * @param primaryAccountNumber The PAN of the card account to return
     * @return The CardAccount specified by the list
     */
    public CardAccount getCardAccount (PrimaryAccountNumber primaryAccountNumber)
    {
        CardAccount cardAccount;
        CardAccount nextCardAccount;

        cardAccount = null;
        nextCardAccount = null;

        // Work through each of the existing card accounts
        for (int i = 0; i < getCardAccounts().size(); i++)
        {
            // Get next card account
            nextCardAccount = getCardAccounts().get(i);

            // Does this card account have the primary account number that we are looking for?
            if (nextCardAccount.getPrimaryAccountNumber().equals(primaryAccountNumber) == true)
            {
                // We have found the card account with the primary account number we are looking for
                cardAccount = nextCardAccount;

                break;
            }
        }

        return cardAccount;
    }

    private void setCardAccounts (ArrayList<CardAccount> cardAccounts)
    {
        this.cardAccounts = cardAccounts;
    }

    private ArrayList<CardAccount> getCardAccounts ()
    {
        return this.cardAccounts;
    }
}