package com.yasenagat.zkweb.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class ZkCfgManagerImpl implements ZkCfgManager {

	private static Logger log = LoggerFactory.getLogger(ZkCfgManagerImpl.class);
	private static ComboPooledDataSource dataSource = new ComboPooledDataSource();
//	private static JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:mysql://127.0.0.1:3306/db_spring_base","root","123456");

	private static Connection conn = null;
	static QueryRunner run = new QueryRunner(H2Util.getDataSource());
	
	public ZkCfgManagerImpl() {
//		cp.setMaxConnections(20);
//		cp.setLoginTimeout(1000 * 50);

		dataSource.setMaxPoolSize(20);
		try {
			dataSource.setLoginTimeout(1000*50);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	};
	private Connection getConnection() throws SQLException{

		log.info("=getConnection=>");
		try {
			if (null == conn) {
//				if(cp ==null){
//					cp = JdbcConnectionPool.create("jdbc:mysql://127.0.0.1:3306/db_spring_base","root","123456");
//				}
//				conn = cp.getConnection();
				conn = dataSource.getConnection("root","123456");
			}
		}catch(Exception ex){
			ex.printStackTrace();
//			System.out.println("=getConnection=>reset conn");
//			cp = JdbcConnectionPool.create("jdbc:mysql://127.0.0.1:3306/db_spring_base","root","123456");
//			conn = cp.getConnection();
		}
		log.info("<=getConnection=>");
		return conn;
	}
	
	private void closeConn(){
		log.info("=closeConn=>");
		if(null != conn){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		log.info("<=closeConn=>");
	}
	
	public boolean init() {
		log.info("=init=>");
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement(ZkCfgManager.initSql);
			return ps.executeUpdate()>0;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("init zkCfg error : {}" , e.getMessage());
		} finally {
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		log.info("<=init=>");
		return false;
	}

	public boolean add(String des, String connectStr, String sessionTimeOut) {
		log.info("=add=>");
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement("INSERT INTO ZK VALUES(?,?,?,?)");
			ps.setString(1, UUID.randomUUID().toString().replaceAll("-", ""));
			ps.setString(2, des);
			ps.setString(3, connectStr);
			ps.setString(4, sessionTimeOut);
			return ps.executeUpdate()>0;
		} catch (SQLException e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
			log.error("add zkCfg error : {}",e.getMessage());
		} finally {
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public List<Map<String, Object>> query() {
		log.info("=query=>");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getConnection().prepareStatement("SELECT * FROM ZK");
			rs = ps.executeQuery();
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			
			ResultSetMetaData meta = rs.getMetaData();
			Map<String, Object> map = null;
			int cols = meta.getColumnCount();
			while(rs.next()){
				map = new HashMap<String, Object>();
				for(int i = 0 ; i < cols ;i++){
					map.put(meta.getColumnName(i+1), rs.getObject(i+1));
				}
				list.add(map);
			}
			
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			conn = null;
//			cp =null;
		} finally {
			if(null != rs){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

		return new ArrayList<Map<String,Object>>();
	}

	public boolean update(String id, String des, String connectStr,
			String sessionTimeOut) {
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement("UPDATE ZK SET DES=?,CONNECTSTR=?,SESSIONTIMEOUT=? WHERE ID=?;");
			ps.setString(1, des);
			ps.setString(2, connectStr);
			ps.setString(3, sessionTimeOut);
			ps.setString(4, id);
			return ps.executeUpdate()>0;
		} catch (Exception e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
			log.error("update id={} zkCfg error : {}",new Object[]{id,e.getMessage()});
		} finally {
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}

	public boolean delete(String id) {
		
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement("DELETE ZK WHERE ID=?");
			ps.setString(1, id);
			return ps.executeUpdate()>0;
		} catch (Exception e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
			log.error("delete id={} zkCfg error : {}",new Object[]{id,e.getMessage()});
		}  finally {
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public Map<String, Object> findById(String id) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getConnection().prepareStatement("SELECT * FROM ZK WHERE ID = ?");
			ps.setString(1, id);
			rs = ps.executeQuery();
			Map<String, Object> map = new HashMap<String, Object>();
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			if(rs.next()){
				for(int i = 0 ; i < cols ;i++){
					map.put(meta.getColumnName(i+1).toLowerCase(), rs.getObject(i+1));
				}
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
		} finally {
			if(null != rs){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public List<Map<String, Object>> query(int page, int rows) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getConnection().prepareStatement("SELECT * FROM ZK limit ?,?");
			ps.setInt(1, (page-1) * rows);
			ps.setInt(2, rows);
			rs = ps.executeQuery();
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			
//			ResultSetMetaData meta = rs.getMetaData();
			Map<String, Object> map = null;
//			int cols = meta.getColumnCount();
			while(rs.next()){
				map = new HashMap<String, Object>();
				for(int i = 0 ; i < rs.getMetaData().getColumnCount() ;i++){
					map.put(rs.getMetaData().getColumnName(i+1), rs.getObject(i+1));
				}
				list.add(map);
			}
			
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
		} finally {
			if(null != rs){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new ArrayList<Map<String,Object>>();
	}

	public boolean add(String id, String des, String connectStr,
			String sessionTimeOut) {
		PreparedStatement ps = null;
		try {
			ps = getConnection().prepareStatement("INSERT INTO ZK VALUES(?,?,?,?);");
			ps.setString(1, id);
			ps.setString(2, des);
			ps.setString(3, connectStr);
			ps.setString(4, sessionTimeOut);
			return ps.executeUpdate()>0;
		} catch (SQLException e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
			log.error("add zkCfg error : {}",e.getMessage());
		} finally {
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		return false;
	}

	public int count() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			 ps = getConnection().prepareStatement("SELECT count(id) FROM ZK");
			 rs = ps.executeQuery();
			 if(rs.next()){
				 return rs.getInt(1);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
			conn =null;
//			cp =null;
			log.error("count zkCfg error : {}",e.getMessage());
		} finally {
			if(null != rs){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(null != ps){
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

}
