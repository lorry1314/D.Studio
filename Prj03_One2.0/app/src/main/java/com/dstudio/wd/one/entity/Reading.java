package com.dstudio.wd.one.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wd824 on 2016/6/20.
 */
public class Reading
{
    private String time;

    // 问答
    @SerializedName("question_id")
    private String questionId;

    @SerializedName("question_title")
    private String questionTitle;

    @SerializedName("answer_title")
    private String answerTitle;

    @SerializedName("answer_content")
    private String answerContent;

    @SerializedName("answer_makettime")
    private String questionMakettime;

    // 短篇
    @SerializedName("content_id")
    private String contentId;

    @SerializedName("hp_id")
    private String hpTitle;

    @SerializedName("hp_makettime")
    private String hpMakettime;

    @SerializedName("guide_word")
    private String guideWord;

    private List<Author> author;

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(String questionId)
    {
        this.questionId = questionId;
    }

    public String getQuestionTitle()
    {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle)
    {
        this.questionTitle = questionTitle;
    }

    public String getAnswerTitle()
    {
        return answerTitle;
    }

    public void setAnswerTitle(String answerTitle)
    {
        this.answerTitle = answerTitle;
    }

    public String getAnswerContent()
    {
        return answerContent;
    }

    public void setAnswerContent(String answerContent)
    {
        this.answerContent = answerContent;
    }

    public String getQuestionMakettime()
    {
        return questionMakettime;
    }

    public void setQuestionMakettime(String questionMakettime)
    {
        this.questionMakettime = questionMakettime;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    public String getHpTitle()
    {
        return hpTitle;
    }

    public void setHpTitle(String hpTitle)
    {
        this.hpTitle = hpTitle;
    }

    public String getHpMakettime()
    {
        return hpMakettime;
    }

    public void setHpMakettime(String hpMakettime)
    {
        this.hpMakettime = hpMakettime;
    }

    public String getGuideWord()
    {
        return guideWord;
    }

    public void setGuideWord(String guideWord)
    {
        this.guideWord = guideWord;
    }

    public List<Author> getAuthor()
    {
        return author;
    }

    public void setAuthor(List<Author> author)
    {
        this.author = author;
    }
}
