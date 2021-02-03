package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "simpleExpenseManager.db";
    private static final String TABLE1_NAME = "ACCOUNTS";
    private static final String T1_C1 = "ACCOUNT_NO";
    private static final String T1_C2 = "BANK";
    private static final String T1_C3 = "ACCOUNT_HOLDER";
    private static final String T1_C4 = "INITIAL_BALANCE";
    private static final String TABLE2_NAME = "TRANSACTIONS";
    private static final String T2_C1 = "LOG_ID";
    private static final String T2_C2 = "DATE";
    private static final String T2_C3 = "ACCOUNT_NO";
    private static final String T2_C4 = "TYPE";
    private static final String T2_C5 = "AMOUNT";

    private static final String TAG = "MyLoggs";



    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE ACCOUNTS (ACCOUNT_NO STRING PRIMARY KEY,BANK STRING,ACCOUNT_HOLDER STRING,INITIAL_BALANCE DOUBLE);");
        sqLiteDatabase.execSQL("CREATE TABLE TRANSACTIONS (LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,DATE TEXT,ACCOUNT_NO STRING,TYPE STRING,AMOUNT DOUBLE,FOREIGN KEY (ACCOUNT_NO) REFERENCES ACCOUNTS (ACCOUNT_NO));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNTS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TRANSACTIONS");
    }

    public List<String> getAccountNumbersList(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery("SELECT ACCOUNT_NO FROM ACCOUNTS;",null);
        List<String> accountNumbers = new ArrayList<>();
        if (result.getCount() == 0){
            return accountNumbers;
        }
        while(result.moveToNext()){
            accountNumbers.add(result.getString(0));
        }
        return accountNumbers;

    }

    public boolean addAccount(Account account){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T1_C1,account.getAccountNo());
        contentValues.put(T1_C2,account.getBankName());
        contentValues.put(T1_C3,account.getAccountHolderName());
        contentValues.put(T1_C4,account.getBalance());
        long result = sqLiteDatabase.insert(TABLE1_NAME,null,contentValues);
        if (result==-1){
            return false;
        }
        return true;


    }

    public boolean addTransaction(Transaction transaction){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T2_C2,transaction.getDate().getTime());
        contentValues.put(T2_C3,transaction.getAccountNo());
        contentValues.put(T2_C4,transaction.getExpenseType().toString());
        contentValues.put(T2_C5,transaction.getAmount());
        long result = sqLiteDatabase.insert(TABLE2_NAME,null,contentValues);
        if (result==-1) {
            return false;
        }
        return true;
    }

    public List<Transaction> getAllTransactionLogs() throws ParseException {
        List<Transaction> transactions = new LinkedList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery("SELECT * FROM TRANSACTIONS;",null);
        if (result.getCount() == 0){
            return transactions;
        }
        while(result.moveToNext()){

            Date date =new Date(result.getLong(1));

            ExpenseType expenseType = ExpenseType.valueOf(result.getString(3));

            double amount = Double.parseDouble(result.getString(4));
            Transaction transaction = new Transaction(date, result.getString(2), expenseType,amount);
            transactions.add(transaction);
        }
        return transactions;
    }

    public List<Account> getAccountsList(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result =  sqLiteDatabase.rawQuery("SELECT * FROM ACCOUNTS;",null);
        List<Account> accountList = new ArrayList<>();
        if (result.getCount() == 0){
            return accountList;
        }
        while(result.moveToNext()){
            double balance = Double.parseDouble(result.getString(3));
           Account account = new Account(result.getString(0),result.getString(1),result.getString(2),balance);
           accountList.add(account);

        }
        return accountList;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM ACCOUNTS WHERE ACCOUNT_NO=?;",new String[]{accountNo});
        if (result.getCount()==0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else{
            result.moveToNext();
            double balance = Double.parseDouble(result.getString(3));
            Account account = new Account(result.getString(0),result.getString(1),result.getString(2),balance);
            return account;
        }
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws Exception {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM ACCOUNTS WHERE ACCOUNT_NO=?;",new String[]{accountNo});
        if (result.getCount()==0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else{
            double newBalance;
            result.moveToNext();
            double currentBalance = Double.parseDouble(result.getString(3));

            switch (expenseType){
                case EXPENSE:
                    newBalance = currentBalance-amount;
                    if (newBalance<0){
                        throw new Exception();
                    }
                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put(T1_C4,String.valueOf(newBalance));
                    int res1 = sqLiteDatabase.update(TABLE1_NAME,contentValues1,"ACCOUNT_NO=?",new String[]{accountNo});
                    break;

                    case INCOME:
                        newBalance = currentBalance+amount;
                        ContentValues contentValues2 = new ContentValues();
                        contentValues2.put(T1_C4,String.valueOf(newBalance));
                        int res2 = sqLiteDatabase.update(TABLE1_NAME,contentValues2,"ACCOUNT_NO=?",new String[]{accountNo});
                        break;
            }
        }
    }

    public void removeAccount(String accountNo) throws InvalidAccountException{
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM ACCOUNTS WHERE ACCOUNT_NO=?;",new String[]{accountNo});
        if (result.getCount()==0){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else{
            int res1 = sqLiteDatabase.delete(TABLE2_NAME,"ACCOUNT_NO=?;",new String[]{accountNo});
            int res2 = sqLiteDatabase.delete(TABLE1_NAME,"ACCOUNT_NO=?;",new String[]{accountNo});
             }

        }




}
