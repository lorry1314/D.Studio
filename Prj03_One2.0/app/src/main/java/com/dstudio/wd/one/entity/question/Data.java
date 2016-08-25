package com.dstudio.wd.one.entity.question;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wd824 on 2016/7/5.
 */
public class Data
{
    @SerializedName("question_id")
    private String questionId;

    @SerializedName("question_title")
    private String questionTitle;

    @SerializedName("question_content")
    private String questionContent;

    @SerializedName("answer_title")
    private String answerTitle;

    @SerializedName("answer_content")
    private String answerContent;

    @SerializedName("question_makettime")
    private String questionMakettime;

    @SerializedName("recommend_flag")
    private String recommenFlag;

    @SerializedName("charge_edt")
    private String chargeEdt;

    @SerializedName("last_update_date")
    private String lastUpdateDate;

    @SerializedName("web_url")
    private String webUrl;

    @SerializedName("read_num")
    private String readNum;

    @SerializedName("guide_word")
    private String guideWord;

    @SerializedName("praisenum")
    private int praiseNum;

    @SerializedName("sharenum")
    private int shareNum;

    @SerializedName("commentnum")
    private int commentNum;

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

    public String getQuestionContent()
    {
        return questionContent;
    }

    public void setQuestionContent(String questionContent)
    {
        this.questionContent = questionContent;
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

    public String getRecommenFlag()
    {
        return recommenFlag;
    }

    public void setRecommenFlag(String recommenFlag)
    {
        this.recommenFlag = recommenFlag;
    }

    public String getChargeEdt()
    {
        return chargeEdt;
    }

    public void setChargeEdt(String chargeEdt)
    {
        this.chargeEdt = chargeEdt;
    }

    public String getLastUpdateDate()
    {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate)
    {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getWebUrl()
    {
        return webUrl;
    }

    public void setWebUrl(String webUrl)
    {
        this.webUrl = webUrl;
    }

    public String getReadNum()
    {
        return readNum;
    }

    public void setReadNum(String readNum)
    {
        this.readNum = readNum;
    }

    public String getGuideWord()
    {
        return guideWord;
    }

    public void setGuideWord(String guideWord)
    {
        this.guideWord = guideWord;
    }

    public int getPraiseNum()
    {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum)
    {
        this.praiseNum = praiseNum;
    }

    public int getShareNum()
    {
        return shareNum;
    }

    public void setShareNum(int shareNum)
    {
        this.shareNum = shareNum;
    }

    public int getCommentNum()
    {
        return commentNum;
    }

    public void setCommentNum(int commentNum)
    {
        this.commentNum = commentNum;
    }
}
