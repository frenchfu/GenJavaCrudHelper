package tw.com.softleader.frnech.fu.GenJavaHelper.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import tw.com.softleader.frnech.fu.GenJavaHelper.common.utils.BeanHump;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.ColumnDetail;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.SettingFromOds;
import tw.com.softleader.frnech.fu.GenJavaHelper.model.TableDetail;

/**
 * @author French.Fu
 * target to gen Java String
 *
 */
@Service
public class ObjectToStringService {

	private final String IS_MODEL = "IS_MODEL";
	private final String IS_VO = "IS_VO";
	private final String IS_MODEL_IDENTITY = "IS_MODEL_IDENTITY";
	private final String IS_IDENTITY_VO = "IS_VO_IDENTITY";
	private final String IS_ENTITY = "IS_ENTITY";
	private final String IS_DAO = "IS_DAO";
	private final String IS_SERVICE = "IS_SERVICE";
	private final String IS_IDENTITY = "IS_IDENTITY";
	private final String TAB ="	";
	private final String NEWLINE = System.lineSeparator();
	
	public static Map<String,String> columnTypeclassMappingMap = Maps.newHashMap();
	
	
	public Map<String, String> scanObjListToJavaCodeMap(SettingFromOds settingFromOds, List<TableDetail> tableDetailObjList) {

		//init
		Map<String, String> resultMap = Maps.newHashMap();
		
		for(TableDetail  tableDetail : tableDetailObjList) {

			Map<String, String> loopUnitMap = this.genObjToJavaCodeMap(settingFromOds,tableDetail);
			resultMap.putAll(loopUnitMap);
			
		}	
		
		return resultMap;
	
	}
	
	public Map<String, String> scanObjListToJavaCodeMapForVo(SettingFromOds settingFromOds,List<TableDetail> tableDetailObjList) {
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		
		for(TableDetail  tableDetail : tableDetailObjList) {
			Map<String, String> loopUnitMap = this.genObjToVoJavaCodeMap(settingFromOds,tableDetail);
			resultMap.putAll(loopUnitMap);				
		}	
		
		return resultMap;
	}

	
	
	private Map<String, String> genObjToVoJavaCodeMap(SettingFromOds settingFromOds, TableDetail tableDetail) {
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.putAll((this.genJavaVoCodeFromTableOnj(settingFromOds,tableDetail)));
		return resultMap;
	}


	private Map<String, String> genObjToJavaCodeMap(SettingFromOds settingFromOds, TableDetail tableDetail) {
		
		//init
		Map<String, String> resultMap = Maps.newHashMap();
		StringBuffer daoSb = new StringBuffer(); 
		StringBuffer serviceSb = new StringBuffer(); 
		
		//TODO interfaceService
		
		resultMap.putAll((this.genJavaEntityStrCodeFromTableOnj(settingFromOds,tableDetail)));
		daoSb.append(this.genJavaDaoStrCodeFromTableOnj(settingFromOds,tableDetail));
		serviceSb.append(this.genJavaServiceStrCodeFromTableOnj(settingFromOds,tableDetail));
		
		//resultMap.put(settingFromOds.getPackageToEntity() +"." + getJavaNameFromTableName(tableDetail.getTableName(),IS_ENTITY) , entitySb.toString());
		resultMap.put(settingFromOds.getPackageToDao().replace(".", "/") +"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_DAO) , daoSb.toString());
		resultMap.put(settingFromOds.getPackageToService().replace(".", "/")+"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_SERVICE) , serviceSb.toString());
		
		return resultMap;
		
	}


	private String genJavaServiceStrCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
		StringBuffer resultSb = new StringBuffer();
		
		StringBuffer importPartSb = new StringBuffer();
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		
		//import part
		importPartSb.append("import org.springframework.beans.factory.annotation.Autowired;").append(NEWLINE);
		importPartSb.append("import org.springframework.stereotype.Service;").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToDao()).append(".").append(className).append("Dao;").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".").append(className).append(";").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".identity.").append(className).append("Identity;").append(NEWLINE);
		
		//Apend str Logic dao part
		//package start
		resultSb.append("package ").append(settingFromOds.getPackageToService()).append(";").append(NEWLINE);
		resultSb.append(importPartSb.toString());
		resultSb.append(NEWLINE);
		resultSb.append("/**").append(NEWLINE);
		resultSb.append("* @author French.Fu").append(NEWLINE);
		resultSb.append("*/").append(NEWLINE);
		resultSb.append("@Service").append(NEWLINE);
		resultSb.append("public class ").append(className).append("Service {").append(NEWLINE);
		resultSb.append(NEWLINE);
		resultSb.append(TAB).append("@Autowired").append(NEWLINE);
		resultSb.append(TAB).append(className).append("Dao dao;").append(NEWLINE);
		resultSb.append(NEWLINE);
		resultSb.append(NEWLINE);
		//get One Method
		resultSb.append(TAB).append("public ").append(className).append(" getOne(").append(className).append("Identity id ) {").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append("return dao.findOne(id);").append(NEWLINE);
		resultSb.append(TAB).append("}").append(NEWLINE);
		//save Method
		resultSb.append(TAB).append("public ").append(className).append(" save(").append(className).append(" entity){").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append("return dao.save(entity);").append(NEWLINE);
		resultSb.append(TAB).append("}").append(NEWLINE);
		resultSb.append(NEWLINE);
		//end
		resultSb.append("}");
		return resultSb.toString();
		
	}



	private String genJavaDaoStrCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
	
		StringBuffer resultSb = new StringBuffer();
		
		StringBuffer importPartSb = new StringBuffer();
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		
		//import part
		importPartSb.append("import org.springframework.data.repository.CrudRepository;").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".").append(className).append(";").append(NEWLINE);
		importPartSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".identity.").append(className).append("Identity;").append(NEWLINE);
		
		//Apend str Logic dao part
		//package start
		resultSb.append("package ").append(settingFromOds.getPackageToDao()).append(";").append(NEWLINE);
		resultSb.append(NEWLINE);
		resultSb.append(importPartSb.toString());
		resultSb.append(NEWLINE);
		resultSb.append("/**").append(NEWLINE);
		resultSb.append("* @author French.Fu").append(NEWLINE);
		resultSb.append("*/").append(NEWLINE);
		resultSb.append("public interface ").append(className).append("Dao extends CrudRepository<")
		.append(className).append(",").append(className).append("Identity> {").append(NEWLINE);
		resultSb.append(NEWLINE);
		resultSb.append("}");
		return resultSb.toString();

	}

	private Map<String,String> genJavaVoCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
		//init
		Map<String,String> resultMap = Maps.newHashMap();
		
		StringBuffer voSb = new StringBuffer();
		StringBuffer identitySb = new StringBuffer();
		String importPartStr = "";
		String classInformationPartStr = "";
		String annotationPartStr = "";
		String embeddedIdPartStr = "";
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		String identityClassName = className + "IdentityVo";
		
		//partString Logic
		annotationPartStr = getAnnotationPartStrForVo(tableDetail,className);
		importPartStr = getImportPartStrForVo(settingFromOds,tableDetail,className);
		classInformationPartStr = getClassInformationPartStr(tableDetail);
		embeddedIdPartStr = getEmbeddedIdPartStrForVo(tableDetail,identityClassName);
		
		
		//Apend str Logic entity part
		//package
		voSb.append("package ").append(settingFromOds.getPackageToVo()).append(";").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(classInformationPartStr)//class Information Part
		.append(annotationPartStr);
		voSb.append("public class ").append(className).append("Vo {").append(NEWLINE)
		.append(NEWLINE)
		.append(embeddedIdPartStr)
		.append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			voSb.append(getColumnPartStrForVo(settingFromOds , columnItem));
		}
		//SET METHOD PART FOR VO need loop
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			if(!StringUtils.trimToEmpty(columnItem.getPk()).contains("PK")) 
				voSb.append(getColumnVoSetPartStr(settingFromOds , columnItem));
		}		
		voSb.append(NEWLINE);
		voSb.append(" }");//entity part End
		
		
		//Apend str Logic Identity part
		//package
		
		identitySb.append("package ").append(settingFromOds.getPackageToVo()).append(".").append("identity;").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(annotationPartStr)
		//.append("@Embeddable").append(NEWLINE)
		.append(classInformationPartStr)//class Information Part
		.append("public class ").append(identityClassName).append(" implements Serializable {").append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			identitySb.append(getIdentityPartStrForVo(settingFromOds , columnItem));
		}		
		identitySb.append(NEWLINE);
		identitySb.append(TAB).append("public ").append(identityClassName).append("(){}").append(NEWLINE);//oring constructer
		//SET METHOD PART FOR VO need loop
		//SET METHOD PART FOR VO need loop
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			if(StringUtils.trimToEmpty(columnItem.getPk()).contains("PK")) 
				identitySb.append(getColumnVoSetPartStr(settingFromOds , columnItem));
		}
		identitySb.append(NEWLINE);
		identitySb.append(" }");//Identity end
		
		
		resultMap.put(settingFromOds.getPackageToVo().replace(".", "/") +"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_VO) , voSb.toString());
		resultMap.put(settingFromOds.getPackageToVo().replace(".", "/") +"/identity/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_IDENTITY_VO) , identitySb.toString());
			
			
		return resultMap;
	}
	

	private Map<String,String> genJavaEntityStrCodeFromTableOnj(SettingFromOds settingFromOds, TableDetail tableDetail) {
		
		//init
		Map<String,String> resultMap = Maps.newHashMap();
		
		StringBuffer entitySb = new StringBuffer();
		StringBuffer identitySb = new StringBuffer();
		String importPartStr = "";
		String classInformationPartStr = "";
		String annotationPartStr = "";
		String annotationPartForIdentityStr = "";
		String embeddedIdPartStr = "";
		String className = getJavaNameFromTableName2(tableDetail.getTableName(),IS_ENTITY);
		String identityOverridePartStr ="";
		String constructPartStr ="";
		String constructPartIdentityStr = "";
		String identityClassName = className + "Identity";
	
		
		//partString Logic
		annotationPartStr = getAnnotationPartStr(tableDetail,className,true);
		annotationPartForIdentityStr = getAnnotationPartStr(tableDetail,className,false);
		importPartStr = getImportPartStr(settingFromOds,tableDetail,className);
		classInformationPartStr = getClassInformationPartStr(tableDetail);
		identityOverridePartStr = getIdentityOverridePartStr(tableDetail,identityClassName);
		embeddedIdPartStr = getEmbeddedIdPartStr(tableDetail,identityClassName);
		constructPartStr = getconstructPartStr(tableDetail, className, identityClassName);
		constructPartIdentityStr = getConstructPartIdentityStr(settingFromOds , tableDetail, className, identityClassName);
		
		
		//Apend str Logic entity part
		//package
		entitySb.append("package ").append(settingFromOds.getPackageToEntity()).append(";").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(annotationPartStr)
		.append(classInformationPartStr);//class Information Part
		entitySb.append("@ApiModel(value = \"").append(className).append("\", description = \"").append(tableDetail.getTableLocalName()).append("\" )").append(NEWLINE);
		entitySb.append("public class ").append(className).append(" {").append(NEWLINE)
		.append(NEWLINE)
		.append(constructPartStr)
		.append(embeddedIdPartStr)
		.append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			entitySb.append(getColumnPartStr(settingFromOds , columnItem));
		}
		
		entitySb.append(" }");//entity part End
		
		
		//Apend str Logic Identity part
		//package
		
		identitySb.append("package ").append(settingFromOds.getPackageToEntity()).append(".").append("identity;").append(NEWLINE)
		.append(NEWLINE)
		.append(importPartStr)//import
		.append(NEWLINE)
		.append(annotationPartForIdentityStr)
		.append("@Embeddable").append(NEWLINE)
		.append("public class ").append(identityClassName).append(" implements Serializable {").append(NEWLINE);
		for( ColumnDetail columnItem : tableDetail.getColumnDetails()){
			identitySb.append(getIdentityPartStr(settingFromOds , columnItem));
		}		
		identitySb.append(NEWLINE);
		identitySb.append(TAB).append("public ").append(identityClassName).append("(){}").append(NEWLINE);//oring constructer
		identitySb.append(identityOverridePartStr);
		identitySb.append(" }");//Identity end
		
		
		resultMap.put(settingFromOds.getPackageToEntity().replace(".", "/") +"/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_ENTITY) , entitySb.toString());
		resultMap.put(settingFromOds.getPackageToEntity().replace(".", "/") +"/identity/" + getJavaNameFromTableName(tableDetail.getTableName(),IS_IDENTITY) , identitySb.toString());


		return resultMap;
	}

	private String getConstructPartIdentityStr(SettingFromOds settingFromOds, TableDetail tableDetail, String className, String identityClassName) {
		//init
		StringBuffer resultSb = new StringBuffer();
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		String pointStr = "";
		
		resultSb.append(TAB).append("public").append(" ").append(identityClassName).append("(");
		for(ColumnDetail columnItem : tableDetail.getColumnDetails()) {
			if(columnItem.getIsPk()) {
				String classType = getColumnClassType(settingFromOds, columnItem);
				String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
				resultSb.append(classType).append(" ").append(javaColumnName).append(pointStr).append(" ");
				pointStr = ",";
			}
		}	
		resultSb.append(") {").append(NEWLINE);
		for(ColumnDetail columnItem : tableDetail.getColumnDetails()) {
			if(columnItem.getIsPk()) {
				String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
				resultSb.append(TAB).append(TAB).append("this.").append(javaColumnName).append(" = ").append(javaColumnName).append(";").append(NEWLINE);
			}
		}
		resultSb.append(TAB).append("}").append(NEWLINE).append(NEWLINE);

		
		return resultSb.toString();
	}
	

	private String getconstructPartStr(TableDetail tableDetail, String className, String identityClassName) {
		//init
		StringBuffer resultSb = new StringBuffer();
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		
		resultSb.append(TAB).append("public").append(" ").append(className).append("(").append(identityClassName).append(" ").append(identityClassNameLowerFirstCharStr).append(") {").append(NEWLINE)
		.append(TAB).append(TAB).append("this.").append(identityClassNameLowerFirstCharStr).append(" = ").append(identityClassNameLowerFirstCharStr).append(";").append(NEWLINE)
		.append(TAB).append("}").append(NEWLINE).append(NEWLINE);
		resultSb.append(TAB).append("public").append(" ").append(className).append("() {};").append(NEWLINE).append(NEWLINE);
		
		return resultSb.toString();
	}

	private String getEmbeddedIdPartStr(TableDetail tableDetail, String identityClassName) {
		//init
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("@EmbeddedId").append(NEWLINE);
		resultSb.append(TAB).append("private ").append(identityClassName).append(" ").append(identityClassNameLowerFirstCharStr).append(";").append(NEWLINE);
		
		return resultSb.toString();
	}

	private String getEmbeddedIdPartStrForVo(TableDetail tableDetail, String identityClassName) {
		//init
		String identityClassNameLowerFirstCharStr = identityClassName.substring(0, 1).toLowerCase() + identityClassName.substring(1);
		
		StringBuffer resultSb = new StringBuffer();
		resultSb.append(TAB).append("private ").append(identityClassName).append(" ").append(identityClassNameLowerFirstCharStr).append(";").append(NEWLINE);
		
		return resultSb.toString();
	}

	private String getColumnPartStr(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(!StringUtils.trimToEmpty(columnItem.getPk()).contains("PK")) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			if(columnItem.getNotNull().contains("V")) {
				resultSb.append(TAB).append("@NotNull").append(NEWLINE);
				if(columnLength != null) {
					resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
				}
				
			}
			resultSb.append(TAB).append("@Column(name=\"").append(columnItem.getColumnName())
			.append("\" ");
			if(columnLength != null) {
				resultSb.append(", length=").append(columnLength);
			}
			resultSb.append(", columnDefinition=\"").append(columnItem.getDataType()).append("\"")
			.append(")").append(NEWLINE);
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
			
		}
		return resultSb.toString();
	}

	private String getColumnPartStrForVo(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(!StringUtils.trimToEmpty(columnItem.getPk()).contains("PK")) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			if(columnItem.getNotNull().contains("V")) {
				resultSb.append(TAB).append("@NotNull").append(NEWLINE);
				if(columnLength != null) {
					resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
				}
				
			}
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
			
		}
		return resultSb.toString();
		
	}
	
	private String getColumnVoSetPartStr(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		String methodName = "set"+ javaColumnName.substring(0, 1).toUpperCase() +javaColumnName.substring(1);
		
			
		resultSb.append(TAB).append("/**").append(NEWLINE);
		resultSb.append(columnDesc);
		if(columnItem.getNotNull().contains("V")) {
			resultSb.append(TAB).append("@NotNull").append(NEWLINE);
			if(columnLength != null) {
				resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
			}
		}
		resultSb.append(TAB).append("*/").append(NEWLINE);
		resultSb.append(TAB).append("public void ").append(methodName).append("( ")
		.append(classType).append(" ").append(javaColumnName).append(" ) {").append(NEWLINE)
		.append(TAB).append(TAB).append("this.").append(javaColumnName).append(" = ").append(javaColumnName).append(";").append(NEWLINE)
		.append(TAB).append("}").append(NEWLINE);;
				
		return resultSb.toString();
	}
	

	private String getIdentityOverridePartStr(TableDetail tableDetail, String identityClassName) {
		//init
		StringBuffer resultSb = new StringBuffer();
		
		//equals rule
		resultSb.append(TAB).append("@Override");
		resultSb.append(TAB).append("public boolean equals(Object o) {").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append("if (this == o) return true;").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append("if (o == null || getClass() != o.getClass()) return false;").append(NEWLINE);
		resultSb.append(TAB).append(TAB).append(identityClassName).append(" that = (").append(identityClassName).append(") o;").append(NEWLINE)
		.append(NEWLINE);
		//loop for pk 
		List<ColumnDetail> pkcolumnDetails = tableDetail.getColumnDetails().stream().filter(c-> !StringUtils.isEmpty(c.getPk()) && c.getPk().contains("PK"))
		.collect(Collectors.toList());
		for(int i = 0 ;  i < pkcolumnDetails.size() ; i++) {
			ColumnDetail pkColumn = pkcolumnDetails.get(i);
			String javaColumnName = BeanHump.underlineToCamel2(pkColumn.getColumnName().toLowerCase());
			if(i<pkcolumnDetails.size()-1) {
				resultSb.append(TAB).append(TAB).append("if (").append(javaColumnName).append(" != null ? !").append(javaColumnName)
				.append(".equals(that.").append(javaColumnName).append(") : that.").append(javaColumnName).append(" != null) return false;").append(NEWLINE);;				
			}else {
				resultSb.append(TAB).append(TAB).append("return (").append(javaColumnName).append(" != null ? !").append(javaColumnName)
				.append(".equals(that.").append(javaColumnName).append(") : that.").append(javaColumnName).append(" != null);").append(NEWLINE);
			}
		}
		resultSb.append(TAB).append("}"); //equals end
		
		//hashCode rule
		resultSb.append(NEWLINE);
		resultSb.append(TAB).append("public int hashCode() {").append(NEWLINE);
		
		for(int i = 0 ;  i < pkcolumnDetails.size() ; i++) {
			ColumnDetail pkColumn = pkcolumnDetails.get(i);
			String javaColumnName = BeanHump.underlineToCamel2(pkColumn.getColumnName().toLowerCase());
			if(i==0) {
				resultSb.append(TAB).append(TAB).append("int result = ").append(javaColumnName).append(" != null ? ").append(javaColumnName).append(".hashCode() : 0;").append(NEWLINE);;
			}else {
				resultSb.append(TAB).append(TAB).append("result = 31 * result + (").append(javaColumnName).append(" != null ? ").append(javaColumnName).append(".hashCode() : 0);").append(NEWLINE);;
			}		
		}
		resultSb.append(TAB).append(TAB).append("return result;").append(NEWLINE);;
		resultSb.append(TAB).append("}").append(NEWLINE);;//hashCode end
		
		return resultSb.toString();
	}
	
	
	
	
	private String getIdentityPartStr(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(StringUtils.trimToEmpty(columnItem.getPk()).contains("PK")) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			resultSb.append(TAB).append("@NotNull").append(NEWLINE);
			resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
			resultSb.append(TAB).append("@Column(name=\"").append(columnItem.getColumnName()).append("\" ");
			resultSb.append(", length=").append(columnLength);
			resultSb.append(", columnDefinition=\"").append(columnItem.getDataType()).append("\"")
			.append(")").append(NEWLINE);
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
		}
		
		
		return resultSb.toString();
	}

	private String getIdentityPartStrForVo(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		//init
		StringBuffer resultSb = new StringBuffer();
		Integer columnLength = getColumnLength(columnItem.getDataType());
		String classType = getColumnClassType(settingFromOds, columnItem);
		String javaColumnName = BeanHump.underlineToCamel2(columnItem.getColumnName().toLowerCase());
		String columnDesc = getColumnDescStr(columnItem);
		
		if(StringUtils.trimToEmpty(columnItem.getPk()).contains("PK")) {
			
			resultSb.append(TAB).append("/**").append(NEWLINE);
			resultSb.append(columnDesc);
			resultSb.append(TAB).append("*/").append(NEWLINE);
			resultSb.append(TAB).append("@NotNull").append(NEWLINE);
			resultSb.append(TAB).append("@Size(min =1, max = ").append(columnLength).append(")").append(NEWLINE);
			resultSb.append(TAB).append("private ").append(classType).append(" ").append(javaColumnName).append(";").append(NEWLINE).append(NEWLINE);
			
		}
		
		
		return resultSb.toString();
	}
	
	


	private String getColumnDescStr(ColumnDetail columnItem) {
		StringBuffer resultSb = new StringBuffer();
		
		if(!StringUtils.isEmpty(columnItem.getColumnName()))
			resultSb.append(TAB).append("*").append("columnName:").append(columnItem.getColumnName().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getColumnLocalName()))
			resultSb.append(TAB).append("*").append("localName:").append(columnItem.getColumnLocalName().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getNotNull()))
			resultSb.append(TAB).append("*").append("notNull:").append(columnItem.getNotNull().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDesc1()))
			resultSb.append(TAB).append("*").append("desc1:").append(columnItem.getDesc1().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDesc2()))
			resultSb.append(TAB).append("*").append("desc2:").append(columnItem.getDesc2().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDefine()))
			resultSb.append(TAB).append("*").append("define:").append(columnItem.getDefine().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getSample()))
			resultSb.append(TAB).append("*").append("sample:").append(columnItem.getSample().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		if(!StringUtils.isEmpty(columnItem.getDefaultValue()))
			resultSb.append(TAB).append("*").append("defaultValue:").append(columnItem.getDefaultValue().replace("\r", " ").replace("\n", " ")).append("<br/>").append(NEWLINE);
		
		
		return resultSb.toString();
	}



	private String getColumnClassType(SettingFromOds settingFromOds, ColumnDetail columnItem) {
		StringBuffer resultSb = new StringBuffer();
		String type = columnItem.getDataType();

		int index1 = type.indexOf("(");
		if(index1 > 0) {
			type = type.substring(0,index1);
		}

		if(settingFromOds.getDbTypeClassMapping().get(type) !=null ) {
			resultSb.append(settingFromOds.getDbTypeClassMapping().get(type));
		}else if(columnTypeclassMappingMap.get(type) !=null ) {
			resultSb.append(columnTypeclassMappingMap.get(type));
		}else {
			resultSb.append("String");
		}
		
		return resultSb.toString();
	}



	private String getJavaNameFromTableName2(String tableName, String isWhat) {
		StringBuffer resultSb = new StringBuffer();
		String javaName = BeanHump.underlineToCamel3(tableName);
		String appendName = "";
		
		switch (isWhat) {
		case IS_ENTITY:
			appendName = "";
			break;
		case IS_DAO:
			appendName = "Dao";
			break;
		case IS_SERVICE:
			appendName = "Service";
			break;
		default:
			break;
		}
		
		resultSb.append(javaName).append(appendName);
		return resultSb.toString();
	}



	private String getJavaNameFromTableName(String tableName, String isWhat) {
		
		StringBuffer resultSb = new StringBuffer();
		String javaName = BeanHump.underlineToCamel3(tableName);
		String appendName = "";
		
		switch (isWhat) {
		case IS_ENTITY:
			appendName = ".java";
			break;
		case IS_DAO:
			appendName = "Dao.java";
			break;
		case IS_SERVICE:
			appendName = "Service.java";
			break;
		case IS_IDENTITY:
			appendName = "Identity.java";
			break;
		case IS_MODEL:
			appendName = "Model.java";
			break;
		case IS_MODEL_IDENTITY:
			appendName = "ModelIdentity.java";
			break;
		case IS_VO:
			appendName = "Vo.java";
			break;
		case IS_IDENTITY_VO:
			appendName = "IdentityVo.java";
			break;			
		default:
			break;
		}
		
		resultSb.append(javaName).append(appendName);
		return resultSb.toString();
		
	}
	
	private Integer getColumnLength(String dataType) {
		Integer result = null;
		int i1 = dataType.indexOf("(");
		int i2 = dataType.indexOf(")");			
		if(i1 > -1 && i2 > i1) {
			String number = dataType.substring(i1+1, i2);
			 try {
		            result = Integer.parseInt(number);
		        } catch (NumberFormatException e){
		          
		        }
		}		
		return result;
	}
	
	

	private String getAnnotationPartStr(TableDetail tableDetail, String className , Boolean isEntity) {
		StringBuffer resultSb = new StringBuffer();
		
		resultSb.append("@SuppressWarnings(\"serial\")").append(NEWLINE);
		resultSb.append("@Getter").append(NEWLINE);
		resultSb.append("@Setter").append(NEWLINE);
		
		resultSb.append("@ToString").append(NEWLINE);
		if(isEntity) {
			resultSb.append("@Table(name = \"").append(tableDetail.getTableName()).append("\")").append(NEWLINE);		
			resultSb.append("@Entity").append(NEWLINE);
		}
		return resultSb.toString();
	}
	
	private String getAnnotationPartStrForVo(TableDetail tableDetail, String className) {
		StringBuffer resultSb = new StringBuffer();
		
		resultSb.append("@SuppressWarnings(\"serial\")").append(NEWLINE);
		resultSb.append("@Getter").append(NEWLINE);
		resultSb.append("@Setter").append(NEWLINE);
		resultSb.append("@ToString").append(NEWLINE);
		
		
		return resultSb.toString();
	}
	
	

	private String getImportPartStr(SettingFromOds settingFromOds, TableDetail tableDetail, String className) {
		
		StringBuffer resultSb = new StringBuffer();
		
		//defaultPart
		resultSb.append("import java.io.Serializable;").append(NEWLINE);
		resultSb.append("import javax.persistence.Embeddable;").append(NEWLINE);
		resultSb.append("import javax.persistence.Column;").append(NEWLINE);
		resultSb.append("import javax.persistence.Id;").append(NEWLINE);
		resultSb.append("import javax.persistence.EmbeddedId;").append(NEWLINE);
		resultSb.append("import javax.persistence.Entity;").append(NEWLINE);
		resultSb.append("import javax.persistence.Table;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.NotNull;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.Size;").append(NEWLINE);
		resultSb.append("import io.swagger.annotations.ApiModel;").append(NEWLINE);
		resultSb.append("import lombok.Getter;").append(NEWLINE);
		resultSb.append("import lombok.Setter;").append(NEWLINE);
		resultSb.append("import lombok.ToString;").append(NEWLINE);
		resultSb.append("import java.math.BigDecimal;").append(NEWLINE);
		resultSb.append("import java.time.LocalDate;").append(NEWLINE);
		resultSb.append("import java.time.LocalDateTime;").append(NEWLINE);
		resultSb.append("import java.util.List;").append(NEWLINE);
		resultSb.append("import java.util.Map;").append(NEWLINE);
		
		resultSb.append("import ").append(settingFromOds.getPackageToEntity()).append(".identity.").append(className).append("Identity;").append(NEWLINE);	
		
		return resultSb.toString();
	}
	
	private String getImportPartStrForVo(SettingFromOds settingFromOds, TableDetail tableDetail, String className) {
		
		StringBuffer resultSb = new StringBuffer();
		
		//defaultPart
		resultSb.append("import java.io.Serializable;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.NotNull;").append(NEWLINE);
		resultSb.append("import javax.validation.constraints.Size;").append(NEWLINE);
		resultSb.append("import lombok.Getter;").append(NEWLINE);
		resultSb.append("import lombok.Setter;").append(NEWLINE);
		resultSb.append("import lombok.ToString;").append(NEWLINE);
		resultSb.append("import java.math.BigDecimal;").append(NEWLINE);
		resultSb.append("import java.time.LocalDate;").append(NEWLINE);
		resultSb.append("import java.time.LocalDateTime;").append(NEWLINE);
		resultSb.append("import java.util.List;").append(NEWLINE);
		resultSb.append("import java.util.Map;").append(NEWLINE);
		
		resultSb.append("import ").append(settingFromOds.getPackageToVo()).append(".identity.").append(className).append("IdentityVo;").append(NEWLINE);	
		
		return resultSb.toString();
	}
	
	
	

	private String getClassInformationPartStr(TableDetail tableDetail) {
		
		String AUTHOR = "@author French.Fu";//TODO MOVE TO PROPERTY		
		
		StringBuffer resultSb = new StringBuffer();
		resultSb.append("/** ").append(NEWLINE);
		resultSb.append("*").append(AUTHOR).append("<br/>").append(NEWLINE);
		resultSb.append("* ").append(tableDetail.getTableLocalName()).append("<br/>").append(NEWLINE);
		resultSb.append("*/ ").append(NEWLINE);
		return resultSb.toString();
	}
	
	
}
