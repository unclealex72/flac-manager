package uk.co.unclealex.music.core.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyMode;

public class AnnotationScopeMetadataResolver extends
		org.springframework.context.annotation.AnnotationScopeMetadataResolver {

	private static Map<String, String> SCOPES_BY_ANNOTATION_NAME = new HashMap<String, String>();
	static {
		SCOPES_BY_ANNOTATION_NAME.put(Prototype.class.getName(), "prototype");
	}
	
	protected ScopedProxyMode scopedProxyMode;

	public AnnotationScopeMetadataResolver() {
		this(ScopedProxyMode.NO);
	}

	public AnnotationScopeMetadataResolver(ScopedProxyMode scopedProxyMode) {
		super(scopedProxyMode);
		this.scopedProxyMode = scopedProxyMode;
	}

	@Override
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		ScopeMetadata metadata = null;
		if (definition instanceof AnnotatedBeanDefinition) {
			AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
			Set<String> annotationTypes = annDef.getMetadata().getAnnotationTypes();
			for (Map.Entry<String, String> entry : SCOPES_BY_ANNOTATION_NAME.entrySet()) {
				if (annotationTypes.contains(entry.getKey())) {
					metadata = new ScopeMetadata();
					metadata.setScopeName(entry.getValue());
				}
			}
		}
		
		if (metadata == null) {
			metadata = super.resolveScopeMetadata(definition);
		}
		else {
			if (!metadata.getScopeName().equals(BeanDefinition.SCOPE_SINGLETON)) {
				metadata.setScopedProxyMode(this.scopedProxyMode);
			}
		}
		return metadata;
	}
}
