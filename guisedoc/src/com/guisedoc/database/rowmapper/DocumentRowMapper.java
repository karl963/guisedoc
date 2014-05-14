package com.guisedoc.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.guisedoc.enums.ErrorType;
import com.guisedoc.object.Document;
import com.guisedoc.object.Product;
import com.guisedoc.workshop.document.settings.Language;

@SuppressWarnings("hiding")
public class DocumentRowMapper<Object> implements RowMapper<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object mapRow(ResultSet rs, int rowNR) throws SQLException {
		try{
			Document document = new Document();
			
			try{document.setID(rs.getLong("ID"));
			}catch(Exception x){}
			try{document.setOrderNR(rs.getString("orderNR"));
			}catch(Exception x){}
			try{document.setShipmentAddress(rs.getString("shipmentAddress"));
			}catch(Exception x){}
			try{document.setShipmentTime(rs.getString("shipmentTime"));
			}catch(Exception x){}
			try{document.setShipmentPlace(rs.getString("shipmentPlace"));
			}catch(Exception x){}
			try{document.setCeSpecification(rs.getString("CeSpecification"));
			}catch(Exception x){}
			try{document.setPaymentRequirement(rs.getString("paymentRequirement"));
			}catch(Exception x){}
			try{document.setValidDue(rs.getLong("validDue"));
			}catch(Exception x){}
			try{document.setAdvance(rs.getDouble("advance"));
			}catch(Exception x){}
			try{document.setPaydInCash(rs.getBoolean("paydInCash"));
			}catch(Exception x){}
			try{document.setShowDiscount(rs.getBoolean("showDiscount"));
			}catch(Exception x){}
			try{document.setAddToStatistics(rs.getBoolean("addToStatistics"));
			}catch(Exception x){}
			try{document.setShowCE(rs.getBoolean("showCE"));
			}catch(Exception x){}
			try{document.setDate(rs.getTimestamp("documentDate"));
			}catch(Exception x){}
			try{document.setClientID(rs.getLong("client_ID"));
			}catch(Exception x){}
			try{document.setLanguage(new Language(rs.getString("languageType")));
			}catch(Exception x){}
			try{document.setType(rs.getString("documentType"));
			}catch(Exception x){}
			try{document.setFullNumber(rs.getString("fullNumber"));
			}catch(Exception x){}
			try{document.setAddToStatistics(rs.getBoolean("addToStatistics"));
			}catch(Exception x){}
			try{document.setVerified(rs.getBoolean("verified"));
			}catch(Exception x){}
			try{document.getClient().getSelectedContactPerson().setID(rs.getLong("contact_person_ID"));
			}catch(Exception x){}
			
			try{document.getClient().setName(rs.getString("clientName"));
			}catch(Exception x){}
			try{document.setTotalSum(rs.getDouble("totalSum"));
			}catch(Exception x){}
			
			return (Object) document;
		}
		catch(Exception x){
			x.printStackTrace();
			return (Object) ErrorType.DATABASE_QUERY;
		}
	}
}