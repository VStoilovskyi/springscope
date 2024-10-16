package org.springscope.scope;

import java.util.List;

@FunctionalInterface
public interface ScopeExtractor {
  List<String> extractScopes();

}
