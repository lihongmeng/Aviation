package com.jxntv.account.model;

/**
 * @author huangwei
 * date : 2021/12/29
 * desc : ugc菜单显示控制
 **/
public class UGCMenuTabModel {

    private boolean favoriteTab;
    private boolean commentTab;
    private boolean questionTab;
    private boolean answerTab;

    public boolean isFavoriteTab() {
        return favoriteTab;
    }

    public void setFavoriteTab(boolean favoriteTab) {
        this.favoriteTab = favoriteTab;
    }

    public boolean isCommentTab() {
        return commentTab;
    }

    public void setCommentTab(boolean commentTab) {
        this.commentTab = commentTab;
    }

    public boolean isQuestionTab() {
        return questionTab;
    }

    public void setQuestionTab(boolean questionTab) {
        this.questionTab = questionTab;
    }

    public boolean isAnswerTab() {
        return answerTab;
    }

    public void setAnswerTab(boolean answerTab) {
        this.answerTab = answerTab;
    }
}
