package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.util.Log;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.InMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpenseManager extends ExpenseManager {
    private DatabaseHelper database;
    public PersistentExpenseManager(DatabaseHelper database){
        this.database = database;
        setup();
    }
    @Override
    public void setup(){
        TransactionDAO persistentMemoryDAO = new PersistentMemoryTransactionDAO(database);
        setTransactionsDAO(persistentMemoryDAO);

        AccountDAO persistentMemoryAccountDAO = new PersistentMemoryAccountDAO(database);
        setAccountsDAO(persistentMemoryAccountDAO);

        // dummy data
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        database.addAccount(dummyAcct1);
        database.addAccount(dummyAcct2);
    }
}
