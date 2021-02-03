package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {
    private DatabaseHelper database;
    private static final String TAG = "MyLogs";


    public PersistentMemoryAccountDAO(DatabaseHelper database){
        this.database = database;

    }
    @Override
    public List<String> getAccountNumbersList() {
        database.getAccountNumbersList();

        return database.getAccountNumbersList();

    }

    @Override
    public List<Account> getAccountsList() {

        return database.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return database.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        database.addAccount(account);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Log.i(TAG,"deleted");
database.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        try {
            database.updateBalance(accountNo,expenseType,amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
