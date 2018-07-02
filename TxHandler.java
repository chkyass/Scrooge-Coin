import java.lang.annotation.Target;
import java.util.ArrayList;

public class TxHandler {


    private UTXOPool ledger;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        this.ledger = new UTXOPool(utxoPool);
    }



    private boolean checkOutSig(ArrayList<Transaction.Input> inputs, Transaction tx) {
        for (int i = 0; i < inputs.size(); i++) {
            UTXO utxo = new UTXO(inputs.get(i).prevTxHash, inputs.get(i).outputIndex);
            if(!Crypto.verifySignature(ledger.getTxOutput(utxo).address, tx.getRawDataToSign(i), inputs.get(i).signature))
                return false;
            if(!ledger.contains(utxo))
                return false;
        }
        return true;
    }

    private boolean checkDoubleSpend(ArrayList<Transaction.Input> inputs) {
        for(int i = 0; i<inputs.size(); i++){
            UTXO utxo = new UTXO(inputs.get(i).prevTxHash, inputs.get(i).outputIndex);
            for(int j = 0; j < inputs.size(); j++){
                if(utxo.equals(new UTXO(inputs.get(i).prevTxHash, inputs.get(i).outputIndex)))
                    return false;
            }
        }
        return true;
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
        // IMPLEMENT THIS
        ArrayList<Transaction.Input> inputs = tx.getInputs();
        ArrayList<Transaction.Output> outputs = tx.getOutputs();

        double sumOu = 0;
        for(int i = 0; i < outputs.size(); i++) {
            if(outputs.get(i).value > 0)
                sumOu += outputs.get(i).value;
            else
                return false;
        }

        double sumIn = 0;
        for(int i = 0; i < inputs.size(); i++) {
            UTXO utxo = new UTXO(inputs.get(i).prevTxHash, inputs.get(i).outputIndex);
            sumIn += ledger.getTxOutput(utxo).value;
        }

        return (sumIn >= sumOu) && checkDoubleSpend(inputs) && checkOutSig(inputs, tx);
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS

        return null;
    }

}
