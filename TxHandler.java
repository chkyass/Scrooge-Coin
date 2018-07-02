import java.lang.annotation.Target;
import java.util.ArrayList;

public class TxHandler {


    private UTXOPool pool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.pool = new UTXOPool(utxoPool);
    }



    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        UTXOPool seen = new UTXOPool();
        double outSum = 0;
        double inSum = 0;

        for(int i = 0; i<tx.numInputs(); i++) {
            Transaction.Input in = tx.getInput(i);
            UTXO utxo = new UTXO(in.prevTxHash, in.outputIndex);

            //Check signature
            if(!Crypto.verifySignature(this.pool.getTxOutput(utxo).address, tx.getRawDataToSign(i), in.signature))
                return false;
            //Check all inputs are valid and no input is consumed more than once
            if(!this.pool.contains(utxo) || seen.contains(utxo))
                return false;

            Transaction.Output prevOut = this.pool.getTxOutput(utxo);
            seen.addUTXO(utxo, prevOut);
            inSum += prevOut.value;
        }

        for (Transaction.Output out : tx.getOutputs()){
            outSum += out.value;
        }

        return outSum <= inSum;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {

        ArrayList<Transaction> transactions =  new ArrayList<>();

        for (int i = 0; i < possibleTxs.length; i++) {
            if(isValidTx(possibleTxs[i])) {
                transactions.add(possibleTxs[i]);

                // Remove spent UTXO from pool
                for(int k = 0; k< possibleTxs[i].numInputs(); k++) {
                    Transaction.Input in = possibleTxs[i].getInput(k);
                    this.pool.removeUTXO(new UTXO(in.prevTxHash, in.outputIndex));
                }

                // Update pool with new UTXO
                for(int j = 0; j < possibleTxs[i].numOutputs(); j++) {
                    this.pool.addUTXO(new UTXO(possibleTxs[i].getHash(), j), possibleTxs[i].getOutput(j));
                }
            }
        }

        return (Transaction[]) transactions.toArray();
    }

}
