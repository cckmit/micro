package micro.commons.util;

import static micro.commons.util.StringUtil.firstStrUpperCase;
import static micro.commons.util.StringUtil.getString;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import micro.commons.annotation.ThreadSafe;
import micro.commons.xml.ReportDto;

/**
 * 复杂xml报文生成工具类（dto的组成参考ReportDto）
 **/
@ThreadSafe
public final class XmlUtils {

	/**
	 * 将dto转换为xml字符串
	 * 
	 * @author gewx
	 * @param report 数据对象
	 * @throws Exception
	 * @return xml字符串
	 **/
	public static String build(ReportDto report) throws Exception {
		return build(report, false);
	}

	/**
	 * 将dto转换为xml字符串
	 * 
	 * @author gewx
	 * @param report   数据对象
	 * @param isFormat 是否格式化xml
	 * @throws Exception
	 * @return xml字符串
	 **/
	@SuppressWarnings("unchecked")
	public static String build(ReportDto report, boolean isFormat) throws Exception {
		Document document = new Document();
		Namespace xmlns = Namespace
				.getNamespace("urn:Declaration:datamodel:standard:CN:" + report.getHead().getMessageType() + ":1");
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		Element rootElement = new Element("Manifest");
		rootElement.addNamespaceDeclaration(xsi);
		rootElement.setNamespace(xmlns);
		document.setRootElement(rootElement);

		for (Field f : report.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			if (List.class.equals(f.getType())) {
				List<Serializable> listDto = emptyIfNull((List<Serializable>) f.get(report));
				listDto.forEach(childDto -> {
					try {
						Element element = childBuild(childDto, rootElement.getNamespace());
						element.setNamespace(rootElement.getNamespace());
						rootElement.addContent(element);
					} catch (Exception ex) {
						throw new RuntimeException("构建报文失败! ex: " + ex.getMessage());
					}
				});
			} else if (f.getType() instanceof Serializable) {
				Serializable dto = (Serializable) f.get(report);
				if (dto != null) {
					Element element = childBuild(dto, rootElement.getNamespace());
					element.setNamespace(rootElement.getNamespace());
					rootElement.addContent(element);
				}
			}
		}

		Optional<XMLOutputter> output = Optional.empty();
		if (isFormat) {
			Format xmlFormat = Format.getPrettyFormat();
			xmlFormat.setEncoding("UTF-8");
			xmlFormat.setIndent(" ");
			output = Optional.of(new XMLOutputter(xmlFormat));
		} else {
			output = Optional.of(new XMLOutputter());
		}

		return output.get().outputString(document);
	}

	/**
	 * 将dto转换为element对象
	 * 
	 * @author gewx
	 * @param dto       数据对象
	 * @param namespace 命名空间
	 * @throws Exception
	 * @return element 元素节点
	 **/
	@SuppressWarnings("unchecked")
	private static Element childBuild(Serializable dto, Namespace namespace) throws Exception {
		Element childRoot = new Element(dto.getClass().getSimpleName());
		for (Field f : dto.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			if (String.class.equals(f.getType())) {
				String text = getString(f.get(dto));
				if (isNotBlank(text)) {
					Element element = new Element(firstStrUpperCase(f.getName()));
					element.setNamespace(namespace);
					element.addContent(text);
					childRoot.addContent(element);
				}
			} else if (List.class.equals(f.getType())) {
				List<Serializable> listDto = emptyIfNull((List<Serializable>) f.get(dto));
				listDto.forEach(childDto -> {
					try {
						Element element = childBuild(childDto, namespace);
						element.setNamespace(namespace);
						childRoot.addContent(element);
					} catch (Exception ex) {
						throw new RuntimeException("构建报文失败! ex: " + ex.getMessage());
					}
				});
			} else if (f.getType() instanceof Serializable) {
				Serializable childDto = (Serializable) f.get(dto);
				if (childDto != null) {
					Element element = childBuild(childDto, namespace);
					element.setNamespace(namespace);
					childRoot.addContent(element);
				}
			}
		}
		return childRoot;
	}

}
