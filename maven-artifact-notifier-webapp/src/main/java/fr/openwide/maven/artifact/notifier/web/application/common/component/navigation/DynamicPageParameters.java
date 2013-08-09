package fr.openwide.maven.artifact.notifier.web.application.common.component.navigation;

import java.util.Map;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Args;

import com.google.common.collect.Maps;

/**
 * Représente des {@link PageParameters} qui ne sont pas figés, et qui en particulier peuvent changer entre deux rafraîchissements en Ajax.<br>
 * Ce mécanisme ne peut pas être implémenté directement au niveau des PageParameters, car on référence des IModels qui doivent être détachés.
 */
public class DynamicPageParameters implements IDetachable {
	
	private static final long serialVersionUID = -9066291686294702275L;
	
	private final PageParameters baseStaticParameters;
	
	private final Map<String, IModel<?>> dynamicParametersMap;
	
	public DynamicPageParameters() {
		this(new PageParameters());
	}
	
	public DynamicPageParameters(PageParameters baseStaticParameters) {
		this.baseStaticParameters = new PageParameters(baseStaticParameters);
		this.dynamicParametersMap = Maps.newLinkedHashMap();
	}
	
	public DynamicPageParameters(DynamicPageParameters copy) {
		this.baseStaticParameters = new PageParameters(copy.baseStaticParameters);
		this.dynamicParametersMap = Maps.newLinkedHashMap(copy.dynamicParametersMap);
	}
	
	public DynamicPageParameters add(String name, IModel<?> valueModel) {
		Args.notNull(name, "name");
		Args.notNull(valueModel, "valueModel");
		
		dynamicParametersMap.put(name, valueModel);
		
		return this;
	}
	
	public PageParameters buildStaticPageParameters() {
		PageParameters result = new PageParameters(baseStaticParameters);
		
		for (Map.Entry<String, IModel<?>> parameter : dynamicParametersMap.entrySet()) {
			Object value = parameter.getValue().getObject();
			if (value != null) {
				result.add(parameter.getKey(), parameter.getValue().getObject());
			}
		}
		
		return result;
	}
	
	@Override
	public void detach() {
		for (Map.Entry<String, IModel<?>> parameter : dynamicParametersMap.entrySet()) {
			parameter.getValue().detach();
		}
	}

}
