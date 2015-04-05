# IPredict, a sequence prediction Framework

![Sequence Prediction Image](http://s9.postimg.org/oihcg1pj3/context_prefix_suffix_1.png)

Sequence prediction consists in predicting the next item(s) of a sequence of items, given a set of training sequences and a finite alphabet of items (symbols). This task has numerous applications such as web page prefetching, product recommendation, weather forecasting and stock market prediction.

### This frameworks has implementations for the following sequence predictors:
* Compact Prediction Tree (CPT) [1]
* Compact Prediction Tree Plus (CPT+) [2]
* First order Markov Chains (PPM) [3] 1
* Dependency Graph (DG) [4] 2
* All-k-Order Markov Chains (AKOM) [5] 3
* TDAG [6] 4
* LZ78 [7] 12

![PPM, DG, and AKOM](http://s8.postimg.org/t1t2ujyid/ppm_dg_akom_1_1.png)


### Datasets

The following datasets are compatible with this framework and can be used to perform experiments with the proposed sequence prediction models.

**BMS** is a popular dataset in the field of association rule mining made
available for KDD CUP 2000. It contains web sessions from an e-commerce
website, encoded as sequences of integers, representing web pages. 
%(parse)

**FIFA** contains web sessions recorded on the 1998 FIFA World Cup Web site and holds over one million web page requests. Originally, the dataset is a set of individual
 requests containing metadata (e.g.  client id and time). We converted requests into sequences by grouping requests by users and splitting a sequence if there was a delay of more than an hour between two requests. Our final dataset is a random sample from the original dataset.
 
**SIGN** is a dense dataset with long sequences, containing 730 sequences of sign-language utterances transcripted from videos.

**KOSARAK** is a dataset containing web sessions from a Hungarian news portal available at http://fimi.ua.ac.be/data. It is the largest dataset used in our experimental evaluation.

**BIBLE** is the religious Christian set of books used in plain text as a flow of sentences. The prediction task consists in predicting the next character in a given sequence of characters. The book is split in sentences where each sentence is a sequence. This dataset is interesting since it has a small alphabet with only 75 distinct characters and it is based on natural language.

***

_[1] T. Gueniche, P. Fournier-Viger, V. S. Tseng, "Compact prediction tree: A lossless modelfor  accurate  sequence  prediction".  Proceedings  of  the  Ninth  Advanced  Data  Mining  and Applications, Springer, 2013, pp. 177-188._

_[2] T. Gueniche, P. Fournier-Viger, R. Raman, V. S. Tseng, "CPT+: Decreasing the time/space complexity of the Compact Prediction Tree". Proceedings of the 19th Pacific-Asia Conference on Knowledge Discovery and Data Mining (PAKDD 2015), Springer, 12 pages (to appear)._

_[3] J. G. Cleary, I. Witten, "Data compression using adaptive coding and partial string matching".IEEE Transactions on Communications, vol. 32, pp. 396-402, 1984._

_[4] V. N. Padmanabhan, J. C. Mogul, "Using predictive prefetching to improve world wide web latency". ACM SIGCOMM Computer Communication Review, vol. 26, pp. 22-36, 1996._

_[5] J.  Pitkow,  P.  Pirolli,  "Mining  longest  repeating  subsequences  to  predict  world  wide  websurfing". Proceedings of the Second USENIX Symposium on Internet Technologies and Systems, 1999, pp. 1._

_[6] P. Laird, R. Saul, "Discrete sequence prediction and its applications". Machine Learning,vol. 15, pp. 43-68, 1994._

_[7] J. Ziv, A. Lempel, "Compression of individual sequences via variable-rate coding". IEEETransactions on Information Theory, vol. 24, pp. 530-536, 1978._
