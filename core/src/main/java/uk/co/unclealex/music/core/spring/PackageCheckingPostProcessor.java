package uk.co.unclealex.music.core.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import uk.co.unclealex.music.base.RequiresPackages;
import uk.co.unclealex.music.base.process.service.ProcessResult;
import uk.co.unclealex.music.base.process.service.ProcessService;

public class PackageCheckingPostProcessor implements BeanFactoryPostProcessor {

	//private static final Logger log = Logger.getLogger(PackageCheckingPostProcessor.class);
	
	private ProcessService i_processService;
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Map<String, Set<String>> beanNamesByMissingRequiredPackageName;
		try {
			beanNamesByMissingRequiredPackageName = findBeanNamesByMissingRequiredPackageName(beanFactory);
		}
		catch (IOException e) {
			throw new FatalBeanException("Could not generate a list of the installed packages.", e);
		}
		if (!beanNamesByMissingRequiredPackageName.isEmpty()) {
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter(writer);
			pw.println("This application could not start as the following required linux packages were not installed:");
			for (Entry<String, Set<String>> entry : beanNamesByMissingRequiredPackageName.entrySet()) {
				pw.format("%s (required by %s)\n", entry.getKey(), StringUtils.join(entry.getValue(), ", "));
			}
			throw new FatalBeanException(writer.toString());
		}
	}

	protected SortedMap<String, Set<String>> findBeanNamesByMissingRequiredPackageName(ConfigurableListableBeanFactory beanFactory)
			throws IOException {
		Map<String, RequiresPackages> packageRequiringBeansByName = listPackageRequiringBeansByName(beanFactory);
		Map<String, Set<String>> beanNamesByRequiredPackageName = new HashMap<String, Set<String>>();
		for (Entry<String, RequiresPackages> entry : packageRequiringBeansByName.entrySet()) {
			String beanName = entry.getKey();
			Collection<String> requiredPackageNames = Arrays.asList(entry.getValue().getRequiredPackageNames());
			for (String requiredPackageName : requiredPackageNames) {
				Set<String> beanNames = beanNamesByRequiredPackageName.get(requiredPackageName);
				if (beanNames == null) {
					beanNames = new TreeSet<String>();
				}
				beanNames.add(beanName);
			}
		}
		Set<String> missingPackageNames = new TreeSet<String>(beanNamesByRequiredPackageName.keySet());
		missingPackageNames.removeAll(listInstalledPackages());
		SortedMap<String, Set<String>> beanNamesByMissingRequiredPackageName = new TreeMap<String, Set<String>>();
		for (String missingPackageName : missingPackageNames) {
			beanNamesByMissingRequiredPackageName.put(missingPackageName, beanNamesByRequiredPackageName.get(missingPackageName));
		}
		return beanNamesByMissingRequiredPackageName;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, RequiresPackages> listPackageRequiringBeansByName(ConfigurableListableBeanFactory beanFactory) {
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, RequiresPackages.class, true, true);
	}

	protected Set<String> listInstalledPackages() throws IOException {
		String dpkgInformation = runDpkg();
		String[] lines = StringUtils.split(dpkgInformation, '\n');
		SortedSet<String> installedPackages = new TreeSet<String>();
		Pattern installedPackagePattern = Pattern.compile("^[a-z][a-z]\\s+(\\S+)");
		for (String line : lines) {
			Matcher matcher = installedPackagePattern.matcher(line);
			if (matcher.find()) {
				String installedPackage = matcher.group(1);
				installedPackages.add(installedPackage);
			}
		}
		return installedPackages;
	}
	
	protected String runDpkg() throws IOException {
		ProcessResult processResult = getProcessService().run(new ProcessBuilder("dpkg", "-l"), true);
		return processResult.getOutput();
	}
	
	public ProcessService getProcessService() {
		return i_processService;
	}

	public void setProcessService(ProcessService processService) {
		i_processService = processService;
	}

}
