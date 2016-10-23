package ru.fizteh.fivt.students.dsalnikov.servlet.database;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.util.HashMap;
import java.util.Map;

public class TransactionManager {

    Map<String, Transaction> transactions;
    TableProvider tableProvider;
    int transactionCounter;

    public TransactionManager(TableProvider tableProvider) {
        transactionCounter = 0;
        transactions = new HashMap<>();
        this.tableProvider = tableProvider;
    }

    public String beginTransaction(String tableName) {
        Transaction transaction = new Transaction(tableProvider, tableName, this);
        transactions.put(transaction.getTransactionId(), transaction);
        return transaction.getTransactionId();
    }

    public Transaction getTransaction(String transactionId) {
        return transactions.get(transactionId);
    }

    public void endTransaction(String transactionId) {
        transactions.remove(transactionId);
    }

    String makeTransactionId() {
        String result = String.format("%05d", transactionCounter);
        transactionCounter += 1;
        return result;
    }
}
