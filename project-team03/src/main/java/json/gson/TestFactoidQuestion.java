package json.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestFactoidQuestion extends TestQuestion {

  @SerializedName("exact_answer")
  private List<String> exactAnswer;

  protected TestFactoidQuestion(String id, String body, QuestionType type, List<String> documents,
          List<Snippet> snippets, List<String> concepts, List<Triple> triples, String idealAnswer,
          List<String> exactAnswer) {
    super(id, body, type, documents, snippets, concepts, triples, idealAnswer);
    this.exactAnswer = exactAnswer;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((exactAnswer == null) ? 0 : exactAnswer.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    TestFactoidQuestion other = (TestFactoidQuestion) obj;
    if (exactAnswer == null) {
      if (other.exactAnswer != null)
        return false;
    } else if (!exactAnswer.equals(other.exactAnswer))
      return false;
    return true;
  }

  public List<String> getExactAnswer() {
    return exactAnswer;
  }

  public void setExactAnswer(List<String> exactAnswer) {
    this.exactAnswer = exactAnswer;
  }

}
