package micro.commons.xml;

import java.util.ArrayList;
import java.util.List;

import micro.commons.util.XmlUtils;

/**
 * 复杂xml报文构建案例
 * 
 * @author gewx
 **/
public class XmlBuildDemo {

	public static void main(String[] args) throws Exception {
		ReportDto report = new ReportDto();
		ReportDto.Head head = new ReportDto.Head();
		head.setMessageType("WLJK_MT3101");
		head.setFunctionCode("2");
		report.setHead(head);

		ReportDto.Declaration declaration = new ReportDto.Declaration();
		report.setDeclaration(declaration);

		ReportDto.Declaration.UnloadingLocation unloadingLocation = new ReportDto.Declaration.UnloadingLocation();
		unloadingLocation.setID("1024");
		unloadingLocation.setArrivalDate("2021/08/25");
		declaration.setUnloadingLocation(unloadingLocation);

		List<ReportDto.Declaration.Consignment> consignment = new ArrayList<>();
		ReportDto.Declaration.Consignment consignment1 = new ReportDto.Declaration.Consignment();
		consignment1.setTotalGrossMassMeasure("总运单1!");

		ReportDto.Declaration.Consignment consignment2 = new ReportDto.Declaration.Consignment();
		consignment2.setTotalGrossMassMeasure("总运单2!");

		consignment.add(consignment1);
		consignment.add(consignment2);

		declaration.setConsignment(consignment);

		ReportDto.Declaration.Consignment.ConsignmentItem item = new ReportDto.Declaration.Consignment.ConsignmentItem();
		item.setSequenceNumeric("1024");

		List<ReportDto.Declaration.Consignment.ConsignmentItem> listItem = new ArrayList<>();
		listItem.add(item);

		ReportDto.Declaration.Consignment.ConsignmentPackaging consignmentPackaging = new ReportDto.Declaration.Consignment.ConsignmentPackaging();
		consignmentPackaging.setQuantityQuantity("10000");
		consignmentPackaging.setTypeCode("货物类型");
		consignment1.setConsignmentPackaging(consignmentPackaging);

		ReportDto.Declaration.Consignment.ConsignmentItem.ConsignmentItemPackaging packaging = new ReportDto.Declaration.Consignment.ConsignmentItem.ConsignmentItemPackaging();
		packaging.setMarksNumbers("唛头");
		item.setConsignmentItemPackaging(packaging);

		ReportDto.Declaration.Consignment.ConsignmentItem.AdditionalInformation additionalInformation = new ReportDto.Declaration.Consignment.ConsignmentItem.AdditionalInformation();
		additionalInformation.setContent("补充协议");
		item.setAdditionalInformation(additionalInformation);

		ReportDto.Declaration.Consignment.ConsignmentItem.Commodity commodity = new ReportDto.Declaration.Consignment.ConsignmentItem.Commodity();
		commodity.setCargoDescription("货物简要描述");
		item.setCommodity(commodity);

		consignment2.setConsignmentItem(listItem);

		String strXml = XmlUtils.build(report,true);
		System.out.println(strXml);
	}
}
