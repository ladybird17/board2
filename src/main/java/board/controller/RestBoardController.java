package board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import board.dto.RestBoardDto;
import board.service.RestBoardService;

@Controller
public class RestBoardController {
	/*
	<REST api>
	- REpersentational State Transfer  의 약자로, 기존의 웹 아키텍처가 HTTP 본래의 우수성을 잘 활용하지 못한다고 생각하여 그 장점을 최대한 활용할 수 있는 아키텍처로 REST를 만듬.
	- HTTP URI로 리소스를 정의하고, HTTP 메서드로 리소스에 대한 행위를 정의한다.
	
	- 리소스 : 서비스를 제공하는 시스템의 자원을 의미한다. 접속주소. URI로 정의 (URI는 명사를 사용)
	
	- 리소스가 ‘/members’와 같이 존재할 경우 기존 방식으로 데이터를 요청할 경우 GET/members/select/1 형태로 사용한다.
	- 기존 방식대로 삭제하고자 하면 GET /members/delete/1 이런 형식을 쓴다.
	- REST 방식으로 사용하면 조회시 GET /member/1 , 삭제시 DELET /members/1 이런 형식으로 사용 가능.
	- URI 부분은 영문 소문자로만 표현함. 가독성을 위해 ‘-‘를 사용 가능. ‘_’는 사용 불가능.
	
	- GET /members –데이터 전송 방식으로 get 방식을 사용 서버에 데이터 요청
	  POST /members –데이터 전송 방식으로 post 방식을 사용하여 서버에 데이터 요청
	  GET /members/1 -
	
	- REST API는 GET , POST , DELETE, PUT에 각각의 기능을 부여하여 사용함
		- POST : CREATE 역할. 리소스를 생성
		- GET : Read 역할. 해당 URI 리소스를 조회함
		- PUT : Update 역할. 해당 URI의 리소스를 수정
		- DELETE : Delete 역할. 해당 URI의 리소스를 삭제

	*/
	@Autowired
	private RestBoardService restBoardService;
	
	@RequestMapping("/")
	public String index() {
		return "/board/index"; /*테스트페이지*/
	}
	
	/*
	@RequestMapping에 value와 method를 사용하여 view 영역과 매칭하고,
	REST api에서 조회 기능을 하는 GET방식을 사용함.
	REST api는 method의 방식에 따라 실행되는 역할이 다르기 때문에
	method 부분이 반드시 필요하다.
	(기존에는 따로 value, method 구분 없이 URI를 적어줬다.)
	@RequestMapping 어노테이션 대신
	@GetMapping, @PostMapping, @PutMapping, @DeleteMapping 어노테이션을 사용하면
	method 부분 생략 가능.
	*/
	
	@RequestMapping(value="/board",method=RequestMethod.GET)
	public ModelAndView openRestBoardList() throws Exception {	//게시판 목록 조회
		//ModelAndView : 사용자 화면과 데이터베이스정보를 가지고있는 클래스(Board1의 BoardController 설명 참조함)
		ModelAndView mv = new ModelAndView("/board/restBoardList");
		
		//인터페이스를 상속받는 ServiceImpl클래스에서 메소드를 사용해야하는데, 위의 @Autowired가 객체생성할 때 BoardServiceImpl의 @Service 어노테이션을 통해 자동연결?생성?해줌
		List<RestBoardDto> list = restBoardService.selectRestBoardList();
		mv.addObject("datas",list);
		
		return mv;
	}
	
	/*
	기존 방식에서는 글번호를 입력할 경우 url을 입력하고
	?파라미터명=파라미터값 을 넣는 형태로 사용했지만, ex)/board.do?boardIdx="1"
	rest api방식에서는 url 이후에 /파라미터값을 넣는 형태로 사용. ex) /board/1
	
	@PathVariable("boardIdx")는 boardIdx라는 파라미터값을 
	uri 부분에 있는{boardIdx} 라는 것과 연동시킴 
	*/
	@RequestMapping(value="/board/{boardIdx}", method=RequestMethod.GET)
	public ModelAndView openRestBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception{//글 상세 페이지 조회
		ModelAndView mv = new ModelAndView("/board/restBoardDetail");
		
		RestBoardDto data = restBoardService.selectRestBoardDetail(boardIdx);
		mv.addObject("data",data);
		
		return mv;
	}
	
	/*
	@RequestMapping에서 주소 부분인 value 값으로 /board/write 를 사용한 
	메서드가 writeRestBoard(), insertRestBoard() 2개가 있지만
	두 메서드는 각각 RequestMethod.GET, RequestMethod.POST를 사용하고 있기 때문에
	서로 다른 기능을 가지고 있음.
	*/
	@RequestMapping(value="/board/write", method=RequestMethod.GET)
	public String writeRestBoard() throws Exception{ //게시글 작성 페이지 조회. 이건 Service에는 없는 메소드.
		return "/board/restBoardWrite";
	}
	
	/*
	 사용자가 form을 통해서 전송한 데이터를 넘겨받아 DB로 다시 전송하는 부분
	 */
	@RequestMapping(value="/board/write", method=RequestMethod.POST)
	public String insertRestBoard(RestBoardDto data) throws Exception{ //작성페이지에서 작성 후 전송
		restBoardService.insertRestBoard(data);
		
		return "redirect:/board";
	}
	
	/*
	 * @중요@
	- REST API를 스프링부트에서 사용할 경우 기본 html은 form에서 지원하는 method가 get과 post 밖에 없기 때문에 오류가 발생함. (수정, 삭제 누르면 오류나는 이유임)
	- 스프링에서 REST API를 지원하기 위해서 HiddenHttpMethodFilter 라는 클래스를 사용하여 GET, POST, PUT, DELETE 등의 여러가지 Method를 지원함
	- 스프링부트2 부터는 기본적으로 HiddenHttpMethodFilter가 등록되어있음.
	- 현재 스프링부트 2.4.3에서는 WebMvcConfigurationSupport를 상속받아 @Bean을 설정해야 정상적으로 사용 가능함.
	- 그러나 이 방식이 불편하기 때문에 application.properties 파일에 spring.mvc.hiddenmethod.filter.enabled=true 를 설정하여 사용함
	 */
	
	
	/*
	@RequestMapping의 method가 PUT이기 때문에 DB 수정 기능을 사용함 
	*/
	@RequestMapping(value="/board/{boardIdx}", method=RequestMethod.PUT)
	public String updateRestBoard(RestBoardDto data) throws Exception{
		//어차피 인덱스값을 가져오기 때문에 PathVariable로 연동시켜주지 않아도 됨
		restBoardService.updateRestBoard(data);
		
		return "redirect:/board";
	}
	
	/*
	@RequestMapping의 method를 DELETE로 하여 DB 삭제 기능 사용 
	*/
	@RequestMapping(value="/board/{boardIdx}", method=RequestMethod.DELETE)
	public String deleteRestBoard(@PathVariable("boardIdx") int boardIdx) throws Exception{
		restBoardService.deleteRestBoard(boardIdx);
		
		return "redirect:/board";
	}
	
}
