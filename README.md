### \[3학년 1학기-모바일 앱 프로그래밍 기말 대체 과제\]

제작 기간 : 2021.05.09~2021.06.16 (39일)

제작 인원 : 1명

### 1, 앱 소개

앱 이름 : 날씨 요정

앱 목적 : 온도별 옷차림을 기록하여, 어느 날씨에 어느 옷을 입을지의 고민을 줄여줌

### 2, 설명

-   사용한 DB : firebase🔥
-   DB에 저장한 내용 :
    -   사용자가 기록한 온도별 옷차림
    -   data class WeatherMemo (  
            var objectId : String,         // 키값  
            var date : String,              // 날짜  
            var temp : String,             // 온도  
            var top : String,                // 상의  
            var bottom : String,         // 하의  
            var outer : String,            // 아우터  
            var memo : String,          // 메모  
            var month : String,          // 월별  
            var tempGroup : String   // 온도별  
        )
-   DB 구조 : 

-   Splash.kt  
    스플래시 화면   
      
    
-   GlobalApplication.kt
-   LoginActivity.kt  
    카카오 로그인  
    메인 액티비티로 넘어갈 때 사용자 이메일 정보 전달   
      
    
-   MainActivity.kt  
    5개의 아이템을 가진 하단 네비게이션 바 
-   기록하기/기록 보기/홈/지역 설정/관심지역 목록  
      
    -   1, WriteActivity.kt (기록하기)  
        HomeFagment에서 현재 온도 가져오기  
        현재 시각, 현재 온도가 미리 입력된 상태에서 현재 옷차림을 기록할 수 있게 함  
        DatePicker Dialogue를 이용하여 날짜 변경 가능  
        EditText를 이용하여 온도 변경 가능  
        모든 EditText에 글이 입력이 되었을 때 <기록하기> 버튼이 활성화  
        <기록하기> 버튼 누르면 firebase🔥에 데이터가 저장됨   
          
        
    -   2, ShowMemoFragment.kt (기록 보기)  
        firebase🔥에 저장된 데이터들을 가져와서 리사이클러뷰로 보여줌, 이때 이메일 정보를 이용해 사용자 데이터만 가져와서 보여줌!  
        토글버튼을 이용하여 온도/날짜별 기록을 확인할 수 있음  
        기록은 ❌버튼을 누르면 삭제됨   
          
        
    -   3, HomeFragment.kt (홈)  
        기본 날씨로 쌍문동의 날씨를 보여줌 (덕성여자대학교 위치)  
        현재 시각의 날씨와 3시간 뒤의 날씨를 보여주고 상세 날씨와 날씨에 따른 추천 옷차림 정보를 보여줌.  
        시단위의 미세먼지 정보를 보여줌  
        날씨/미세먼지 농도에 따라서 이미지 변경  
        ⭐을 누르면 관심 지역으로 등록되고, 다시 누르면 해제됨  
          
        
    -   4, FindAreaFragment.kt (지역 설정)  
        날씨 정보가 궁금한 지역을 찾아 HomeFragment에 장소 정보 전달   
          
        
    -   5, InterestAreaFragment.kt (관심 지역 목록)  
        사용자가 선택한 관심 지역 목록을 보여줌  
        관심 지역을 클릭하면 해당 지역의 날씨 정보로 HomeFragment를 변경  
        ❌버튼 누르면 삭제됨 
-    MyMapViewEventListener.kt   
      
    
-   WeatherMomo.kt  
    firebase🔥에 저장할 날씨 기록  
    날씨 정보들
-   InterestArea.kt  
    관심지역 정보들   
      
    
-   WEATHER.kt
-   WeatherInterface.kt  
    날씨 API 정보 가져오기
-   AIR\_POLLUTION.kt
-   AirPollutionInterface.kt  
    미세먼지 API 정보 가져오기   
      
    
-   WeatherMemoAdapter.kt  
    리사이클러뷰에 옷차림 기록들을 보여주기 위한 어댑터
-   InterestAreaAdapter.kt  
    리사이클러뷰에 관심 지역을 보여주기 위한 어댑터

기타

-   액티비티 전환 애니메이션
-   날씨 API(기상청\_동네예보 조회서비스), 미세먼지 API (한국환경공단\_에어코리아\_대기오염정보), 카카오맵 API 사용
-   데이터 삭제 가능
-   커스텀 

### 3, 느낀점

짧은 기간이었지만 정말 즐겁게 작업했다.

앱을 처음 만들어 보는 것이어서 내게 의미가 큰 프로젝트다.

구현에 성공하거나 실패해도 다 즐거운 경험이었다.

### 4, 어려웠던 점

모르는 것들은 구글링으로 잘 해결할 수 있어서 딱히 어려운 점은 없었다.

사용자 UI도 내가 만들었는데, 내가 UI를 잘 제작하지 못해서 중간중간 바꾸고 통일하는 과정이 어렵다기보단...귀찮았다.

### 5, 아쉬운 점

-   스피너 높이 제한을 두고 싶었는데 구현하지 못한 것
-   앱을 종료했을 때 저장이 되지 못하는 것
-   프레그먼트간 데이터 전달 시 전역변수로 전달한 것
-   사용자의 실시간 위치를 받아오고 싶었는데 구현할 시간이 부족했던 것
-   로그인 API를 다양하게 하고 싶었는데 구현할 시간이 부족했던 것
-   팀플이었으면 결과물 질도 좋아지고 협업 경험이 생겼을 수도 있었는데 1인 프로젝트 과제였던 것
