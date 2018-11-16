import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.sql.*;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import com.mysql.cj.protocol.Message;
public class IclassDB {
	private static String path;
	private static File file = null;
	private static PrintWriter pw = null;
	private static PreparedStatement pre = null;
	private static ResultSet re = null;
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// TODO Auto-generated method stub
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/iclass?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true","root","123456");
		Statement statement = conn.createStatement();
		try {
			int port = 6666;
			
			ServerSocket server = new ServerSocket(port);
			System.out.println("程序已经启动，开始监听" + port + "端口");
			Socket socket;
			InputStream in;
			InputStreamReader reader;
			BufferedReader br;
			String info = "";
			while (true){
				socket = server.accept();
				in = socket.getInputStream();
				reader = new InputStreamReader(in);
				br = new BufferedReader(reader);
				info = br.readLine();
				StringTokenizer st = new StringTokenizer(info,"_");
				String command = st.nextToken();
				if (command.equals("register")){
					System.out.println(info);
					String stu_id = st.nextToken();
					String pass = st.nextToken();
					String sql = "insert into user (stu_id, password,loction_switch,longitude,latitude,study_time) values (" + stu_id + "," + pass + "," + "0" + "," + "0" + "," + "0" + "," + "0" +");";
					statement.execute(sql);
					System.out.println("成功注册用户" + stu_id);
				}else if (command.equals("login")){
					boolean isExist = false;
					String stu_id = st.nextToken();
					String pass = st.nextToken();
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					while (rs.next()){
						if (rs.getString("stu_id").equals(stu_id) && rs.getString("password").equals(pass)){
							isExist = true;
						}
					}
					socket.shutdownInput();
					socket = server.accept();
					OutputStream out = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(out);
					if (isExist){
						pw.write("YES");
						System.out.println("找到用户"  + stu_id);
					}else {
						pw.write("NO");
						System.out.println("未能找到用户"  + stu_id);
					}
					pw.flush();
					pw.close();
					out.close();
				}else if (command.equals("getInfo")){
					System.out.println(info);
					String name = "";
					String phoneNum = "";
					String sex = "";
					String ver = "";
					String returnData = "";
					int switch_state = 0;
					String stu_id = st.nextToken();
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					while (rs.next()){
						if (rs.getString("stu_id").equals(stu_id)){
							name = rs.getString("name");
							phoneNum = rs.getString("phoneNum");
							sex = rs.getString("sex");
							ver = rs.getString("ver");
							switch_state = rs.getInt("loction_switch");
							break;
						}
					}
					socket.shutdownInput();
					returnData = name+"_"+sex+"_"+phoneNum+"_"+ver+"_"+switch_state;
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					System.out.println(returnData);
					pw.write(returnData);
					pw.flush();
					pw.close();
				}else if (command.equals("changeInfo")){
					System.out.println(info);
					String stu_id = st.nextToken();
					String name = st.nextToken();
					String sex = st.nextToken();
					String phoneNum = st.nextToken();
					int ver = Integer.parseInt(st.nextToken());
					String sql = "update user set name='" + name +"',sex='" + sex + "',phoneNum='" + phoneNum + "' where stu_id='" + stu_id + "'";
					System.out.println(sql);
					statement.execute(sql);
				}else if (command.equals("delete")){
					String stu_id = st.nextToken();
					String sql = "delete from user where stu_id = " + stu_id;
					statement.execute(sql);
				}else if(command.equals("lost")) {
					System.out.println(info);
					String name = st.nextToken();
					int addr = Integer.parseInt(st.nextToken());
					String lost_name = st.nextToken();
					String contract = st.nextToken();
					String base64 = st.nextToken();
					
					String temp = "";
					while(true) {
						temp = br.readLine();
						if(temp.equals("ok")) {
							break;
						}
						base64 +=( "\n" + temp);
					}
					file = new File("C:\\lost_image\\lost_" + name + "_" + lost_name +".txt");
					pw = new PrintWriter(new FileOutputStream(file));
					pw.print(base64);
					System.out.println("base64格式图片已经保存到:" + file.getPath());
					String sql = "insert into lost (name,addr,lost_name,contract) values('" + name + "'," + addr + ",'" + lost_name + "','" + contract + "');";
					statement.execute(sql);
				}else if(command.equals("getlostitem")){
					System.out.println(info);
					String nouse = st.nextToken();
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					String sql = "select * from lost";
					String return_lost_item = "";
					pre = conn.prepareStatement(sql);
					re = pre.executeQuery();
					while(re.next()) {
						String name = re.getString(2);
						int addr = re.getInt(3);
						String lost_name = re.getString(4);
						String contract = re.getString(5);
						String lost_return = name + "_" + addr + "_" + lost_name + "_" +  contract;
						return_lost_item += (lost_return + "_");
					}
					return_lost_item += "ok";
					pw.write(return_lost_item);
					pw.flush();
					System.out.println(return_lost_item);
				}else if(command.equals("getswitchstate")) {
					System.out.println(info);
					String returnData = "";
					int switch_state = 0;
					String stu_id = st.nextToken();
					String name = "";
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					while (rs.next()){
						if (rs.getString("stu_id").equals(stu_id)){
							switch_state = rs.getInt("loction_switch");
							name = rs.getString("name");
							break;
						}
					}
					socket.shutdownInput();
					returnData = name + "_" + switch_state;
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					pw.write(returnData);
					System.out.println(returnData);
					pw.flush();
					pw.close();
				}else if (command.equals("setswitchstate")){
					System.out.println(info);
					String stu_id = st.nextToken();
					int switchstate = Integer.parseInt(st.nextToken());
					String sql = "update user set loction_switch='" + switchstate+ "' where stu_id='" + stu_id + "'";
					System.out.println(sql);
					statement.execute(sql);
				}else if(command.equals("pushgpsloc")) {
					System.out.println(info);
					String id = st.nextToken();
					String longitude = st.nextToken();
					String latitude = st.nextToken();
					String sql = "update user set longitude=" + longitude+ ",latitude=" + latitude + " where stu_id='" + id + "'";
					System.out.println(sql);
					statement.execute(sql);
				}else if(command.equals("increasestudytime")) {
					System.out.println(info);
					String id = st.nextToken();
					int minute = Integer.parseInt(st.nextToken());
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					int current_time = 0;
					while (rs.next()){
						if (rs.getString("stu_id").equals(id)){
							current_time = rs.getInt("study_time");
							break;
						}
					}
					minute += current_time;
					String sql1 = "update user set study_time = " + minute + " where stu_id = '" + id + "'";
					statement.execute(sql1);
					System.out.println(sql1);
				}else if(command.equals("getstudytime")) {
					System.out.println(info);
					String id = st.nextToken();
					String returnData = "";
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					int temp_time = 0;
					while (rs.next()){
						if (rs.getString("stu_id").equals(id)){
							temp_time = rs.getInt("study_time");
							break;
						}
					}
					sql = "select * from mes";
					rs = statement.executeQuery(sql);
					int mes_count = 0;
					while(rs.next()) {
						if(rs.getString("to_id").equals(id)) {
							mes_count ++;
						}
					}
					socket.shutdownInput();
					returnData = id + "_" + temp_time + "_" + mes_count;
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					pw.write(returnData);
					System.out.println(returnData);
					pw.flush();
					pw.close();
				}else if(command.equals("getlostfromstr")) {
					System.out.println(info);
					String str = st.nextToken();
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					String sql = "select * from lost";
					String return_lost_item = "";
					pre = conn.prepareStatement(sql);
					re = pre.executeQuery();
					while(re.next()) {
						String name = re.getString(2);
						int addr = re.getInt(3);
						String lost_name = re.getString(4);
						String contract = re.getString(5);
						if(lost_name.indexOf(str) != -1) {
							String lost_return = name + "_" + addr + "_" + lost_name + "_" +  contract;
							return_lost_item += (lost_return + "_");
						}
					}
					return_lost_item += "ok";
					pw.write(return_lost_item);
					pw.flush();
					pw.close();
					System.out.println(return_lost_item);
				}else if(command.equals("getinfofromid")) {
					System.out.println(info);
					String id = st.nextToken();
					socket.shutdownInput();
					socket = server.accept();
					String name = "";
					String phoneNum = "";
					String sex = "";
					String returnData = "";
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					while (rs.next()){
						if (rs.getString("stu_id").equals(id)){
							name = rs.getString("name");
							phoneNum = rs.getString("phoneNum");
							sex = rs.getString("sex");
							break;
						}
					}
					returnData = name+"_"+sex+"_"+phoneNum;
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					System.out.println(returnData);
					pw.write(returnData);
					pw.flush();
					pw.close();
				}else if(command.equals("getAllLocation")) {
					socket.shutdownInput();
					String id;
					double latitude;
					double longitude;
					socket = server.accept();
					String name;
					String returnData = "";
					String sql = "select * from user";
					ResultSet rs = statement.executeQuery(sql);
					while (rs.next()){
						
						id = rs.getString("stu_id");
						name = rs.getString("name");
						longitude = rs.getDouble("longitude");
						latitude = rs.getDouble("latitude");
						if(longitude != 0 && latitude != 0) {
							returnData += id + "_" + name + "_" + longitude + "_" + latitude + "_";
						}
					}
					returnData += "ok";
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					pw.write(returnData);
					pw.flush();
					pw.close();
					System.out.println(returnData);
				}else if(command.equals("uploadmes")) {
					System.out.println(info);
					String from_id = st.nextToken();
					String to_id = st.nextToken();
					String mes = st.nextToken();
					String sql = "insert into mes (from_id,to_id,mes) values ('" + from_id + "','" + to_id + "','" + mes + "');";
					statement.execute(sql);
					System.out.println(sql);
				}else if(command.equals("getmesdetail")) {
					System.out.println(info);
					String id = st.nextToken();
					socket.shutdownInput();
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					String from_id;
					String to_id;
					String mes;
					String sql = "select * from mes";
					ResultSet rs = statement.executeQuery(sql);
					String returnData = "";
					while(rs.next()) {
						from_id = rs.getString("from_id");
						to_id = rs.getString("to_id");
						mes = rs.getString("mes");
						if(to_id.equals(id)) {
							returnData += from_id + "_" + mes + "_";
						}
					}
					returnData += "ok";
					System.out.println(returnData);
					pw.write(returnData);
					pw.flush();
					pw.close();
				}else if(command.equals("pushpagenum")) {
					System.out.println(info);
					socket.shutdownInput();
					int page_num = Integer.parseInt(st.nextToken());
					String sql = "select * from page";
					ResultSet rs = statement.executeQuery(sql);
					boolean isHave = false;
					int page_num_temp,temp_temp = 0;
					while(rs.next()) {
						page_num_temp = rs.getInt("pagenum");
						temp_temp = rs.getInt("num");
						if(page_num_temp == page_num) {
							isHave = true;
						}
					}
					if(isHave) {
						temp_temp ++;
						String sqll = "update page set num = " + temp_temp +" where pagenum = " + page_num +";";
						statement.execute(sqll);
					}else {
						String sqll = "insert into page (pagenum,num) values(" + page_num + ",1);";
						statement.execute(sqll);
					}
					
				}else if(command.equals("refresh")) {
					System.out.println(info);
					socket.shutdownInput();
					socket = server.accept();
					PrintWriter pw = new PrintWriter(socket.getOutputStream());
					String sql = "select * from page";
					ResultSet rs = statement.executeQuery(sql);
					String returnData = "";
					while(rs.next()) {
						int pagenum = rs.getInt("pagenum");
						int num = rs.getInt("num");
						returnData += pagenum + "_" + num + "_";
					}
					returnData += "ok";
					System.out.println(returnData);
					pw.write(returnData);
					pw.flush();
					pw.close();
					socket.close();
				}else if(command.equals("checkout")){
					System.out.println(info);
					String sql = "delete from page;";
					statement.execute(sql);
					socket.close();
				}
				br.close();
				reader.close();
				in.close();
				socket.close();
			}
//			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
