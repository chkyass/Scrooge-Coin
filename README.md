# Transactions organisation
<pre>      
Transaction
|input  |
|input  |     Transaction
|output |---->|input  |
|       |     |       |
|output |     |ouput  |
|       |     |-------|
|       |     Transaction
|output |---->|input  |
|-------|     |       |
              |output |
              |-------|
</pre>
              
# Data structures

input: Signature of the spender + UTX hash + UTXO index.  
output: public key and value .  
UTXO: superset of output, transaction hash + index in the transaction .  
UTXOPool: Hashmap<Hash, Transaction output> .  
