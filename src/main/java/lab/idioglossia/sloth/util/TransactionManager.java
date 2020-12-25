package lab.idioglossia.sloth.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionManager {
    private List<Transaction> transactions = new ArrayList<>();
    private volatile boolean started = false;
    private final Lock rollbackLock = new ReentrantLock();
    private volatile boolean rollingBack = false;
    private final Config config;

    public TransactionManager(){
        this(new Config());
    }

    public TransactionManager(Config config) {
        this.config = config;
    }


    public synchronized TransactionManager addTransaction(Transaction transaction) throws TransactionAlreadyRunning {
        if(started || rollingBack)
            throw new TransactionAlreadyRunning();
        transaction.setTransactionManager(this);
        transactions.add(transaction);
        return this;
    }

    public void commit(){
        if(started || rollingBack)
            return;
        started = true;
        for (Transaction transaction : transactions) {
            if(rollingBack)
                return;
            try {
                transaction.process();
            }catch (RuntimeException e){
                rollbackAll(transaction);
                started = false;
                throw e;
            }
        }
        started = false;
    }

    private void rollbackAll(Transaction transaction){
        boolean locked = rollbackLock.tryLock();
        if(!locked)
            return;
        rollingBack = true;
        int transactionIndex = transactions.indexOf(transaction);
        for(int i = (transactionIndex - 1); i > -1; i--){
            if(config.isIgnoreRollbackExceptions())
                try {
                    transactions.get(i).onRollBack();
                }catch (Exception ignored){}
            else
                transactions.get(i).onRollBack();
        }
        rollingBack = false;
    }

    static public abstract class Transaction {
        private TransactionManager transactionManager;
        public abstract void process();
        public void onRollBack(){}

        protected final void rollback(){
            transactionManager.rollbackAll(this);
        }

        private void setTransactionManager(TransactionManager transactionManager){
            this.transactionManager = transactionManager;
        }
    }

    public class TransactionAlreadyRunning extends Exception {
        public TransactionAlreadyRunning() {
            super("You cant add another transaction to already running transaction manager");
        }
    }

    public static class Config {
        private boolean ignoreRollbackExceptions = false;

        public Config setIgnoreRollbackExceptions(boolean ignoreRollbackExceptions) {
            this.ignoreRollbackExceptions = ignoreRollbackExceptions;
            return this;
        }

        public boolean isIgnoreRollbackExceptions() {
            return ignoreRollbackExceptions;
        }
    }
}
