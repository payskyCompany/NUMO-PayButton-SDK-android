package io.paysky.paybutton.data.network;

import io.paysky.paybutton.data.model.response.DateTransactionsItem;

public interface CheckTransactionListener {

    void transactionSuccess(DateTransactionsItem transactionsItem);

    void transactionFailed();

    void transactionNotFound();

    void onError(Throwable throwable);
}
