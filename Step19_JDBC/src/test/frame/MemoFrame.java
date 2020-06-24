package test.frame;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import test.dao.MemberDao;
import test.dao.MemoDao;
import test.dto.MemberDto;
import test.dto.MemoDto;

/*
 *  CREATE TABLE MEMO
 *  (num NUMBER PRIMARY KEY,
 *  content VARCHAR2(30),
 * 	regdate DATE)    
 * 
 *
 * 	CREATE SEQUENCE MEMO_SEQ;
 * 
 * 	위와 같이 테이블과 시퀀스를 만들고 해당 테이블에 데이터를
 * 	SELECT, INSERT, UPDATE, DELETE 기능을 수행할수 있는 MemoFrame 을 만들어 보세요.
 * 
 * 	조건
 * 1.num 칼럼의 값은 시퀸스를 이용해서 넣으세요.
 * 2.regdate 칼럼(등록일)의 값은 SYSDATE를 이용해서 넣으세요.
 * 3.수정은 content 만 수정 가능하게 하세요.
 * 4.MemoDto, MemoDao 를 만들어서 프로그래밍 하세요.
 */
public class MemoFrame extends JFrame implements ActionListener,PropertyChangeListener{
	//필드
	JTextField inputContent;
	JTable table;
	DefaultTableModel model;
	//생성자
	public MemoFrame() {
		//레이아웃을 보더 레이아웃으로 한다
		setLayout(new BorderLayout());
		//레이블과 필드 추가
		JLabel label1=new JLabel("Content");
		inputContent=new JTextField(10);
		//버튼 추가
		JButton saveBtn=new JButton("저장");
		//버튼에 액션커맨드,리스너 추가(클래스에 임플리먼츠해야함)
		saveBtn.setActionCommand("save");
		saveBtn.addActionListener(this);
		
		//삭제 버튼 추가
		JButton deleteBtn=new JButton("삭제");
		//버튼에 액션커맨드,리스너 추가(클래스에 임플리먼츠해야함)
		deleteBtn.setActionCommand("delete");
		deleteBtn.addActionListener(this);
		//패널을 만들고 패널에 레이블,필드,버튼들 추가
		JPanel panel=new JPanel();
		panel.add(label1);
		panel.add(inputContent);
		panel.add(saveBtn);
		panel.add(deleteBtn);
		//프레임에 판넬 추가해서 위쪽으로 배치
		add(panel, BorderLayout.NORTH);
		//테이블을 새로 만든다.
		table=new JTable();
		//테이블 칼럼으로 num,content,date를 넣는다.
		String[] colNames= {"Num", "Content", "Date"};
		//테이블에 출력할 정보를 가지고 있는 모델객체
		model=new DefaultTableModel(colNames, 0) {
			@Override//인자로 전달되는 row,column이 수정가능한지 알려주는 메소드
			public boolean isCellEditable(int row, int column) {
				if(column==0 || column==2) {
					return false;
				}else {
					return true;
				}
			}
		};
		//모델을 테이블에 연결한다.
		table.setModel(model);
		//스크롤이 가능 하도록 테이블을 JScrollPane 에 감싼다.
		JScrollPane scroll=new JScrollPane(table);
		//JScrollPane  을 프레임의 가운데에 배치하기 
		add(scroll, BorderLayout.CENTER);
		table.addPropertyChangeListener(this);
		displayMemo();
	}
	//table에 메모리스트를 보여주는 메소드
	public void displayMemo() {
		//row  의 갯수를 강제로 0 으로 지정해서 삭제 한다. 
		model.setRowCount(0);
		//메모 목록을 얻어와서 
		MemoDao dao=MemoDao.getInstance();
		List<MemoDto> list=dao.getList();
		for(MemoDto tmp:list) {
			//MemoDto 객체에 저장된 정보를 Object[] 객체에 순서대로 담는다.
			Object[] row= {tmp.getNum(), tmp.getContent(), tmp.getRegdate()};
			model.addRow(row);
		}
	}
	public static void main(String[] args) {
		MemoFrame f=new MemoFrame();
		//JFrame extends 해야함.
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		f.setBounds(100, 100, 800, 500);
		f.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command=e.getActionCommand();
		if(command.equals("save")) {//전송버튼을 눌렀을때
			//input에 입력한 내용을 변수에 담기
			String content=inputContent.getText();
			//위의 content 변수에 담긴 문자열을 Dto에 있는 Content필드에 저장
			MemoDto dto=new MemoDto();
			dto.setContent(content);
			//Dao객체를 만들어서 dao에 만들어둔 insert메소드로 DB에 저장
			MemoDao dao=MemoDao.getInstance();
			dao.insert(dto);
			//저장한 정보까지 테이블에 띄우기
			displayMemo();
		}else if(command.equals("delete")) {//삭제버튼을 눌렀을때
			//테이블에 선택된 로우의 인덱스를 알아내기
			int selectIndex=table.getSelectedRow();
			if(selectIndex==-1) {//선택된 로우가 없다면 -1
				return;//여기서 끝
			}
			int num=(int)model.getValueAt(selectIndex, 0);//알아낸 인덱스의 0번칼럼의 값 알아내기
			int selection=JOptionPane.showConfirmDialog(this, "정말 삭제하시겠습니까?", "ALERT", JOptionPane.YES_NO_OPTION);
			if(selection==JOptionPane.NO_OPTION) {//메세지를 띄워서 아니요를 눌렀을때
				return;//여기서 끝
			}
			MemoDao dao=MemoDao.getInstance();
			dao.delete(num);
			displayMemo();
		}
	}
	boolean isEditing=false;
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("property change!");
		System.out.println(evt.getPropertyName());
		if(evt.getPropertyName().equals("tableCellEditor")) {
			if(isEditing) {//수정중일때
				//변화된 값을 읽어와서 DB에 반영한다.
				//수정된 칼럼에 있는 row 전체의 값을 읽어온다.
				int selectedIndex=table.getSelectedRow();
				int num=(int)model.getValueAt(selectedIndex, 0);
				String content=(String)model.getValueAt(selectedIndex, 1);
				//수정할 메모의 정보를 MemoDto 객체에 담고
				MemoDto dto=new MemoDto();
				dto.setNum(num);
				dto.setContent(content);
				//DB에 저장하기
				MemoDao.getInstance().update(dto);
				isEditing=false;//수정중이 아니라고 표시한다
			}
			isEditing=true;//수정중이라 표시한다
		}
	}
}
