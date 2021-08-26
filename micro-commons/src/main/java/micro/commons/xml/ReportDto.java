package micro.commons.xml;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 复杂N级嵌套XML的生成案例Dto
 * 
 * @author gewx
 **/
@Setter
@Getter
public class ReportDto implements Serializable {

	private static final long serialVersionUID = 3602374426724901412L;

	/**
	 * 报文头
	 **/
	private Head head;

	/**
	 * 报文体
	 **/
	private Declaration declaration;

	/**
	 * 报文头定义
	 **/
	@Setter
	@Getter
	public static class Head implements Serializable {

		private static final long serialVersionUID = 2733250333773905918L;

		/**
		 * 报文编号
		 **/
		private String messageID;

		/**
		 * 报文功能代码
		 **/
		private String functionCode;

		/**
		 * 报文类型代码
		 **/
		private String messageType;

		/**
		 * 发送方代码
		 **/
		private String senderID;

		/**
		 * 接受方代码
		 **/
		private String receiverID;

		/**
		 * 发送时间
		 **/
		private String sendTime;

		/**
		 * 报文版本
		 **/
		private String version;

	}

	/**
	 * 报文头定义
	 **/
	@Setter
	@Getter
	public static class Declaration implements Serializable {

		private static final long serialVersionUID = 5072950670011301650L;

		/**
		 * 申报地海关代码
		 **/
		private String declarationOfficeID;

		/**
		 * 运输工具信息
		 **/
		private BorderTransportMeans borderTransportMeans;

		/**
		 * 卸货地
		 **/
		private UnloadingLocation unloadingLocation;

		/**
		 * 提运单
		 **/
		private List<Consignment> consignment;

		/**
		 * 补充信息
		 **/
		private AdditionalInformation additionalInformation;

		/**
		 * 补充信息定义
		 **/
		@Setter
		@Getter
		public static class AdditionalInformation implements Serializable {

			private static final long serialVersionUID = -4039677184501302542L;

			/**
			 * 备注
			 **/
			private String content;

			/**
			 * 航线标记
			 **/
			private String lineFlag;

			/**
			 * 境内船名
			 **/
			private String nativeShipName;

			/**
			 * 境内航次
			 **/
			private String nativeVoyageNo;

			/**
			 * 境内运输方式
			 **/
			private String nativeTrafCode;
		}

		/**
		 * 提运单信息定义
		 **/
		@Setter
		@Getter
		public static class Consignment implements Serializable {

			private static final long serialVersionUID = -4871756233035255730L;

			/**
			 * 运输合同信息
			 **/
			private TransportContractDocument transportContractDocument;

			/**
			 * 运输合同附加
			 **/
			private AssociatedTransportDocument associatedTransportDocument;

			/**
			 * 提运单包装信息
			 **/
			private ConsignmentPackaging consignmentPackaging;

			/**
			 * 货物总毛重
			 **/
			private String totalGrossMassMeasure;

			/**
			 * 托运货物详细信息
			 **/
			private List<ConsignmentItem> consignmentItem;

			/**
			 * 托运货物详细信息定义
			 **/
			@Setter
			@Getter
			public static class ConsignmentItem implements Serializable {

				private static final long serialVersionUID = 1773631903785946492L;

				/**
				 * 托运货物序号
				 **/
				private String sequenceNumeric;

				/**
				 * 托运货物包装信息
				 **/
				private ConsignmentItemPackaging consignmentItemPackaging;

				/**
				 * 货物简要信息描述
				 **/
				private Commodity commodity;

				/**
				 * 补充信息
				 **/
				private AdditionalInformation additionalInformation;

				/**
				 * 补充信息定义
				 **/
				@Setter
				@Getter
				public static class AdditionalInformation implements Serializable {

					private static final long serialVersionUID = 6921808137970615925L;

					/**
					 * 补充信息
					 **/
					private String content;
				}

				/**
				 * 货物简要信息描述定义
				 **/
				@Setter
				@Getter
				public static class Commodity implements Serializable {

					private static final long serialVersionUID = 2780176001515158626L;

					/**
					 * 货物简要描述
					 **/
					private String cargoDescription;
				}

				/**
				 * 托运货物包装信息定义
				 **/
				@Setter
				@Getter
				public static class ConsignmentItemPackaging implements Serializable {

					private static final long serialVersionUID = 953299268460848169L;

					/**
					 * 唛头
					 **/
					private String marksNumbers;
				}
			}

			/**
			 * 提运单包装信息定义
			 **/
			@Setter
			@Getter
			public static class ConsignmentPackaging implements Serializable {

				private static final long serialVersionUID = 7662927276608875911L;

				/**
				 * 托运货物件数
				 **/
				private String quantityQuantity;

				/**
				 * 包装种类代码
				 **/
				private String typeCode;

			}

			/**
			 * 运输合同附加信息定义
			 **/
			@Setter
			@Getter
			public static class AssociatedTransportDocument implements Serializable {

				private static final long serialVersionUID = 8406923400762539236L;
				/**
				 * 分提运单号
				 **/
				private String iD;
			}

			/**
			 * 运输合同信息定义
			 **/
			@Setter
			@Getter
			public static class TransportContractDocument implements Serializable {

				private static final long serialVersionUID = 718798274485256966L;

				/**
				 * 总提运单号
				 **/
				private String iD;
			}
		}

		/**
		 * 卸货地信息定义
		 **/
		@Setter
		@Getter
		public static class UnloadingLocation implements Serializable {

			private static final long serialVersionUID = -8812930463581226879L;

			/**
			 * 卸货地代码
			 **/
			private String iD;

			/**
			 * 到达卸货地日期
			 **/
			private String arrivalDate;
		}

		/**
		 * 运输工具信息定义
		 **/
		@Setter
		@Getter
		public static class BorderTransportMeans implements Serializable {

			private static final long serialVersionUID = -6786138878659969936L;

			/**
			 * 航次航班编号
			 **/
			private String journeyID;

			/**
			 * 运输方式代码
			 **/
			private String typeCode;

			/**
			 * 运输工具代码
			 **/
			private String iD;

			/**
			 * 运输工具名称
			 **/
			private String name;
		}
	}
}
