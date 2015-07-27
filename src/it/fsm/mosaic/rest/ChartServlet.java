package it.fsm.mosaic.rest;

import it.fsm.mosaic.util.DatabaseUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;






@SuppressWarnings("serial")
public class ChartServlet extends HttpServlet{


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req,resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException{ 
		String patientName = req.getParameter("patientName");
		String callback = req.getParameter("callback");
		resp.setContentType("text/javascript");
		//System.out.println("IN SERVLET " + patientName);
		PrintWriter out = resp.getWriter();

		JSONObject obj = new JSONObject();
		JSONArray jsonPatientArray = new JSONArray();
		InputStream input = null;
		Connection conn = null;
		ResultSet rs = null;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			//tolgo caratteri speciale
			//System.out.println("Apostrofo "+patientName.indexOf("'"));
			patientName = patientName.replaceAll("'", "\\\\'");
			//System.out.println("Nome corretto: "+patientName);
			String[] patientToken = patientName.split(" ");
			conn = DatabaseUtil.getMosaicNewConnection();
			Statement stmt = conn.createStatement();
			String sql = "select patient_id, name, surname, date_of_birth from Patient where ";
			if(patientToken.length==1){ //o cognome o nome
				sql = sql.concat("(name like '%").concat(patientToken[0]).concat("%' or surname like '%").concat(patientToken[0]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");
			}else if(patientToken.length==2){ //provo le combinazioni nomeCognome o cognomeNome
				sql = sql.concat("(name like '%").concat(patientToken[0]).concat("%' and surname like '%").concat(patientToken[1]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");
				sql = sql.concat(" union select patient_id, name, surname, date_of_birth from Patient where (name like '%").concat(patientToken[1]).concat("%' and surname like '%").concat(patientToken[0]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");
			}else if (patientToken.length>2){
				sql = sql.concat("(name like '%").concat(patientToken[0]).concat("%' and surname like '%").concat(patientToken[1]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");
				sql = sql.concat(" union select patient_id, name, surname, date_of_birth from Patient where (name like '%").concat(patientToken[1]).concat("%' and surname like '%").concat(patientToken[0]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");

			}
			System.out.println(sql);		
			rs = stmt.executeQuery(sql);
			boolean somethingFound = false;
			while(rs.next()){
				somethingFound = true;
				JSONObject jsonPatient = new JSONObject();
				jsonPatient.put("patientId",rs.getString("patient_id"));
				jsonPatient.put("patientSurname",rs.getString("surname"));
				jsonPatient.put("patientName", rs.getString("name"));
				if(rs.getDate("date_of_birth")!=null){
					String dobString = dateFormatter.format(rs.getDate("date_of_birth"));
					jsonPatient.put("patientDOB",dobString);
				}
				jsonPatientArray.add(jsonPatient);
				//System.out.println(rs.getString("surname")+" "+rs.getString("name")+" "+rs.getString("patient_id")+" "+rs.getDate("date_of_birth"));		
			}
			if(!somethingFound){
				//System.out.println("RICERCA ESTESA");
				sql = "select patient_id, name, surname, date_of_birth from Patient where ";
				for(int i=0; i < patientToken.length; i++){
					if(i==0){
						sql = sql.concat("(name like '%").concat(patientToken[i]).concat("%' or surname like '%").concat(patientToken[i]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");
					}else{
						sql = sql.concat(" union select patient_id, name, surname, date_of_birth from Patient where (name like '%").concat(patientToken[i]).concat("%' or surname like '%").concat(patientToken[i]).concat("%') and patient_id in (select distinct patient_id from Diabetic_Disease)");
					}
				}
				//System.out.println(sql);		
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					JSONObject jsonPatient = new JSONObject();
					jsonPatient.put("patientId",rs.getString("patient_id"));
					jsonPatient.put("patientSurname",rs.getString("surname"));
					jsonPatient.put("patientName", rs.getString("name"));
					if(rs.getDate("date_of_birth")!=null){
						String dobString = dateFormatter.format(rs.getDate("date_of_birth"));
						jsonPatient.put("patientDOB",dobString);
					}
					jsonPatientArray.add(jsonPatient);
					//System.out.println(rs.getString("surname")+" "+rs.getString("name")+" "+rs.getString("patient_id")+" "+rs.getDate("date_of_birth"));		
				}
			}

			obj.put("patients", jsonPatientArray);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);

			out.println(callback + "("+swout.toString()+ ");");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}

				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
