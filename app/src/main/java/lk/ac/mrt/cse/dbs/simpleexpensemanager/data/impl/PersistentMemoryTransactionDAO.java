package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private DatabaseHelper database;


    public PersistentMemoryTransactionDAO(DatabaseHelper database){
        this.database = database;
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        if (transaction.getAmount()>database.getAccount(accountNo).getBalance() && transaction.getExpenseType().equals(ExpenseType.EXPENSE)){
            throw new InvalidAccountException("Not enough Money");
        }
        else{
            boolean res = database.addTransaction(transaction);
        }

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        try {
            List<Transaction> transactions = database.getAllTransactionLogs();
            return transactions;
        } catch (ParseException e) {

            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = null;
        try {
            transactions = database.getAllTransactionLogs();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
