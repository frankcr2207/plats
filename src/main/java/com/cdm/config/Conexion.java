package com.cdm.config;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Conexion {
	
	public DriverManagerDataSource ConectarMySQL(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://172.28.206.25:3306/csjar");
        //dataSource.setUrl("jdbc:mysql://172.28.206.19:3306/csjar_prueba");
        dataSource.setUsername("usercsjar");
        dataSource.setPassword("Admin5I0J$$");
        return dataSource;
    }
	
	public DriverManagerDataSource ConectarMySQLAlimentos(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://172.28.206.25:3306/sioj_alimentos");
        //dataSource.setUrl("jdbc:mysql://172.28.206.19:3306/sioj_alimentos_prueba");
        dataSource.setUsername("usercsjar");
        dataSource.setPassword("Admin5I0J$$");
        return dataSource;
    }
	
	public DriverManagerDataSource ConectarMySQLElecciones(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://172.28.206.25:3306/elecciones");
        dataSource.setUsername("usercsjar");
        dataSource.setPassword("Admin5I0J$$");
        return dataSource;
    }
	
	public DriverManagerDataSource ConectarMySQLConcurso(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://172.28.206.20:3306/concurso");
        dataSource.setUsername("usercsjar");
        dataSource.setPassword("Admin5I0J$$");
        return dataSource;
    }
	
	public DriverManagerDataSource ConectarSybase(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
        dataSource.setUrl("jdbc:sybase:Tds:172.28.0.7:3949/sij_001_04");
        dataSource.setUsername("integra99");
        dataSource.setPassword("159753123456789123");
        return dataSource;
    }
	
	/*public DriverManagerDataSource ConectarSybase(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
        dataSource.setUrl("jdbc:sybase:Tds:172.28.190.110:3951/SIJ11_001_04_01");
        dataSource.setUsername("dba");
        dataSource.setPassword("sql");
        return dataSource;
    }
	
	public DriverManagerDataSource ConectarSybase(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
        dataSource.setUrl("jdbc:sybase:Tds:172.28.0.128:7969/SIJ11_001_04_01");
        dataSource.setUsername("usr_informatica");
        dataSource.setPassword("++159753123789**");
        return dataSource;
    }
	public DriverManagerDataSource ConectarSybase(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.sybase.jdbc4.jdbc.SybDriver");
        dataSource.setUrl("jdbc:sybase:Tds:172.28.0.6:30410/SIJ_001_04_Alterno");
        dataSource.setUsername("userrq");
        dataSource.setPassword("Ar3quipa");
        return dataSource;
    }*/

}
