package com.dstudio.wd.one.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dstudio.wd.one.R;
import com.dstudio.wd.one.entity.Author;
import com.dstudio.wd.one.entity.Reading;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wd824 on 2016/6/21.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    private LinkedList<Reading> readingList;
    private MyItemClickListener myItemClickListener;
    private int type = 0;
    private static final int ESSAY = 1;
    private static final int QA = 2;

    public MyAdapter(LinkedList<Reading> readingList)
    {
        super();
        this.readingList = readingList;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = View.inflate(parent.getContext(), R.layout.item_reading, null);
        return new ViewHolder(view, myItemClickListener, type);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String date = "";
        switch (type)
        {
            case 0:
                date = readingList.get(position).getTime();
                break;
            case ESSAY:
                date = readingList.get(position).getHpMakettime().substring(0, 10);
                break;
            case QA:
                date = readingList.get(position).getQuestionMakettime().substring(0, 10);
                break;
            default:
                break;
        }
        holder.txtTime.setText(date);
        holder.txtHpTitle.setText(readingList.get(position).getHpTitle());
        List<Author> authorList = readingList.get(position).getAuthor();
        if (authorList != null)
        {
            holder.txtHpAuthor.setText(authorList.get(0).getUserNmae());
        }
        holder.txtGuideWord.setText(readingList.get(position).getGuideWord());
        holder.txtQuestionTitle.setText(readingList.get(position).getQuestionTitle());
        holder.txtAnswerTitle.setText(readingList.get(position).getAnswerTitle());
        holder.txtAnswerContent.setText(readingList.get(position).getAnswerContent());
    }

    public void setOnItemClickListener(MyItemClickListener listener)
    {
        this.myItemClickListener = listener;
    }

    @Override
    public int getItemCount()
    {
        return readingList.size();
    }

    public Reading getItem(int position)
    {
        return readingList.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView txtTime;
        public TextView txtHpTitle;
        public TextView txtHpAuthor;
        public TextView txtGuideWord;
        public TextView txtQuestionTitle;
        public TextView txtAnswerTitle;
        public TextView txtAnswerContent;

        public CardView cardView;
        public LinearLayout essayLayout;
        public LinearLayout questionLayout;

        public MyItemClickListener myItemClickListener;

        public ViewHolder(View itemView, MyItemClickListener myItemClickListener, int type)
        {
            super(itemView);
            this.myItemClickListener = myItemClickListener;
            txtTime = (TextView) itemView.findViewById(R.id.item_time);
            txtHpTitle = (TextView) itemView.findViewById(R.id.hp_title);
            txtHpAuthor = (TextView) itemView.findViewById(R.id.hp_author);
            txtGuideWord = (TextView) itemView.findViewById(R.id.guide_word);
            txtQuestionTitle = (TextView) itemView.findViewById(R.id.question_title);
            txtAnswerTitle = (TextView) itemView.findViewById(R.id.answer_title);
            txtAnswerContent = (TextView) itemView.findViewById(R.id.answer_content);

            cardView = (CardView) itemView.findViewById(R.id.item_card_view);
            essayLayout = (LinearLayout) itemView.findViewById(R.id.item_essay);
            questionLayout = (LinearLayout) itemView.findViewById(R.id.item_question);

            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 0);
            cardView.setLayoutParams(layoutParams);

            essayLayout.setOnClickListener(this);
            questionLayout.setOnClickListener(this);

            if (type == ESSAY)
            {
                questionLayout.setVisibility(View.GONE);
            }
            else if (type == QA)
            {
                essayLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view)
        {
            if (myItemClickListener != null)
            {
                myItemClickListener.onItemClick(view, getPosition());
            }
        }
    }
}
