package tn.esprit.examen.nomPrenomClasseExamen.entities.LearnIt;

public class IAEvaluationResult {
    private Integer scoreIA;
    private String commentaireIA;

    public IAEvaluationResult(Integer scoreIA, String commentaireIA) {
        this.scoreIA = scoreIA;
        this.commentaireIA = commentaireIA;
    }
    public IAEvaluationResult() {
    }

    public Integer getScoreIA() {
        return scoreIA;
    }

    public void setScoreIA(Integer scoreIA) {
        this.scoreIA = scoreIA;
    }

    public String getCommentaireIA() {
        return commentaireIA;
    }

    public void setCommentaireIA(String commentaireIA) {
        this.commentaireIA = commentaireIA;
    }
}
