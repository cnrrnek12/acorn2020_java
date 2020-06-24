package test.dto;

public class MemoDto {
	//할일을 적은 메모 하나의 필드 생성
	private int num;
	private String content;
	private String regdate;
	//default생성자 만들기
	public MemoDto() {}
	//인자로 필드에 저장할 값을 전달받는 생성자
	public MemoDto(int num, String content, String regdate) {
		super();
		this.num = num;
		this.content = content;
		this.regdate = regdate;
	}
	//필드의 접근 메소드 setter,getter 메소드
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
}
