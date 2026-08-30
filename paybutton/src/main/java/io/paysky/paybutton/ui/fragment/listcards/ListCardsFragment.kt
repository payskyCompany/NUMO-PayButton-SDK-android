package io.paysky.paybutton.ui.fragment.listcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.paysky.paybutton.R
import io.paysky.paybutton.data.model.TokenizedCardPaymentParameters
import io.paysky.paybutton.data.model.response.CardItem
import io.paysky.paybutton.ui.activity.payment.PaymentActivity
import io.paysky.paybutton.ui.base.BaseFragment
import io.paysky.paybutton.ui.fragment.cardsOperations.CardsPresenter
import io.paysky.paybutton.ui.fragment.cardsOperations.CardsView
import io.paysky.paybutton.ui.fragment.managecards.ManageCardsFragment
import io.paysky.paybutton.ui.fragment.manualpayment.ManualPaymentFragment
import io.paysky.paybutton.ui.fragment.paymentprocessing.PaymentProcessingFragment
import io.paysky.paybutton.util.AppConstant
import io.paysky.paybutton.util.ToastUtils
import io.paysky.paybutton.util.hideSoftKeyboard


class ListCardsFragment : BaseFragment(), CardsView {
    private lateinit var presenter: CardsPresenter<CardsView>
    private lateinit var addNewCardButton: Button
    private lateinit var proceedButton: Button
    private lateinit var backButton: Button
    private lateinit var manageCardsButton: Button
    private lateinit var cardsList: RecyclerView
    private lateinit var adapter: SavedCardsAdapter

    init {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Stay invisible until the session/cards result arrives; otherwise this
        // screen flashes briefly before falling back to manual card entry.
        view.visibility = View.INVISIBLE
        activity = getActivity() as PaymentActivity
        activity.setHeaderIconClickListener { activity.finish() }
//        activity.showPaymentInfoAndOptions()

        presenter = CardsPresenter(arguments, this)
        initView(view)
    }

    private fun initView(view: View) {
        addNewCardButton = view.findViewById(R.id.add_new_card_button)
        addNewCardButton.setOnClickListener {
            goToManualPaymentFragment(addToBackStack = true)
        }

        adapter = SavedCardsAdapter(
            onSubmitDataValid = {
                // For 19-digit cards, CVV is not required, pass empty string
                val cvv = it.cvv ?: ""
                moveToPaymentProcessing(it.cardId, cvv)
            },
            onChangeItem = {
                val mHandler = activity.window.decorView.handler
                mHandler.post {
                    adapter.notifyItemChanged(it)
                }
            },
            activity = activity
        )
        cardsList = view.findViewById(R.id.cards_list)
        cardsList.layoutManager = LinearLayoutManager(this.context)
        cardsList.adapter = adapter

        proceedButton = view.findViewById(R.id.proceed_button)
        proceedButton.setOnClickListener {
            adapter.submit()
        }

        backButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            activity.finish()
        }
        manageCardsButton = view.findViewById(R.id.manage_cards_button)
        manageCardsButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, presenter.paymentData)
            activity.replaceFragmentAndAddOldToBackStack(ManageCardsFragment::class.java, bundle)
        }
    }

    private fun goToManualPaymentFragment(addToBackStack: Boolean) {
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, presenter.paymentData)
        if (addToBackStack) {
            activity.replaceFragmentAndAddOldToBackStack(ManualPaymentFragment::class.java, bundle)
        } else {
            activity.replaceFragmentAndRemoveOldFragment(ManualPaymentFragment::class.java, bundle)
        }
    }

    private fun moveToPaymentProcessing(cardId: Int, cvv: String) {
        hideSoftKeyboard(activity)
        val tokenizedCardPaymentParameters = TokenizedCardPaymentParameters(cardId, cvv)
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.BundleKeys.PAYMENT_DATA, presenter.paymentData)
        bundle.putParcelable(AppConstant.BundleKeys.TOKENIZED_CARD, tokenizedCardPaymentParameters)
        activity.replaceFragmentAndRemoveOldFragment(PaymentProcessingFragment::class.java, bundle)
    }

    override fun showToastError(message: String) {
        if (!isAdded) return
        ToastUtils.showLongToast(context, message)
        // Never leave the user on an empty cards screen — card entry still works.
        goToManualPaymentFragment(addToBackStack = false)
    }

    override fun onCustomerNotFound() {
        if (!isAdded) return
        // The backend doesn't know this customer id, so treat it as a customer
        // with no saved cards. Drop the id so it is not sent as TokenCustomerId
        // in PayByCard; a save-card payment will get a fresh one from the backend.
        presenter.paymentData?.customerId = null
        goToManualPaymentFragment(addToBackStack = false)
    }

    override fun showToastErrorAndFinish(error: Int) {
        ToastUtils.showLongToast(context, getString(error))
        activity.finish()
    }

    override fun showSavedCards(cardsLists: List<CardItem>) {
        adapter.setItems(cardsLists)
        if (cardsLists.isEmpty()) {
            goToManualPaymentFragment(addToBackStack = false)
        } else {
            view?.visibility = View.VISIBLE
        }
    }
}