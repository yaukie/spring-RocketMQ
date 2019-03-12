import java.sql.*;     //导入java.sql包  
public class test {//创建test类，保证文件名与类名相同  
           Connection con;//声明Connection对象  
          Statement sql;   
          ResultSet res;  
          public void testConnection() {  //建立返回值为Connection的方法  
              try {        //加载数据库驱动类  
                  Class.forName("oracle.jdbc.driver.OracleDriver");  
                  System.out.println("数据库驱动加载成功");  //返回加载驱动成功信息  
              }catch(ClassNotFoundException e){  
                  e.printStackTrace();  
              }  
              try {  
                  con=DriverManager.getConnection("jdbc:oracle:" + "thin:@192.168.200.17:12574:daqnation","dshare","DaqsOft20171116Dssswqwes3431");//通过访问数据库的URL获取数据库连接对象 ，这里后两个参数分别是数据库的用户名及密码 
                  System.out.println("数据库连接成功");  //返回连接成功信息
              }catch(SQLException e) {  
				  System.out.println("数据库连接失败"+e.getMessage());  //返回连接成功信息
              }  
          }  
		  
          public static void main(String[] args) {   //主方法  
              test c = new test();    //创建本类方法  
               c.testConnection();//调用连接数据库的方法  
          }  
}  
 