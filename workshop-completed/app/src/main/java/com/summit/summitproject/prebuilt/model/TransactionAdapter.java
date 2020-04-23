package com.summit.summitproject.prebuilt.model;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.summit.summitproject.R;

import java.util.List;

/**
 * A {@link RecyclerView.Adapter} is used with a {@link RecyclerView}. It takes in the data which
 * should be displayed in the list and tells the UI how each individual piece of data should be
 * rendered.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    /**
     * The list of credit card transactions which will be adapted to the UI.
     */
    private List<Transaction> transactions;

    /**
     * A listener to deliver callbacks to whenever a transaction in the list is clicked.
     */
    private TransactionClickedListener listener;

    /**
     * Takes in the list of transactions that should be rendered and a listener to receive callbacks
     * if the user clicks on a particular row.
     */
    public TransactionAdapter(List<Transaction> transactions, TransactionClickedListener listener) {
        this.transactions = transactions;
        this.listener = listener;
    }

    /**
     * Called when the UI needs the a new row (at {position}) to be <b>created</b>. In this case,
     * all of our rows look the same, so we just inflate the same layout for all rows.
     * <br>
     * The new row isn't filled with data yet, that's done by
     * {@link TransactionAdapter#onBindViewHolder(ViewHolder, int)}
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_transaction, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called when the UI needs the next row (at {position}) to be <b>filled with data</b> rendered
     * and passes the {@link ViewHolder} which should be filled with data.
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // Use the transaction at index {position} to set up the row's UI widgets
        holder.merchant.setText(transactions.get(position).getMerchant());
        holder.amount.setText(transactions.get(position).getAmount());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inform the click listener that this row was clicked and pass the Transaction
                // associated with this row.
                if (listener != null) {
                    listener.onTransactionClicked(transactions.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    /**
     * Used to determine how many rows the list should be in total.
     */
    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * Holds the UI widgets which will comprise a single row in the list (to render
     * a {@link Transaction}).
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        TextView merchant;

        TextView amount;

        ViewHolder(View rootView) {
            super(rootView);
            cardView = rootView.findViewById(R.id.card_container);
            merchant = rootView.findViewById(R.id.merchant);
            amount = rootView.findViewById(R.id.amount);
        }
    }

    /**
     * Will receive callbacks whenever a transaction in the list is clicked.
     */
    public interface TransactionClickedListener {
        void onTransactionClicked(Transaction transaction);
    }
}
