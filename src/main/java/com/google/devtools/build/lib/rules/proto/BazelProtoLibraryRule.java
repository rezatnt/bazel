// Copyright 2015 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.rules.proto;

import static com.google.devtools.build.lib.packages.Attribute.attr;
import static com.google.devtools.build.lib.packages.BuildType.LABEL;
import static com.google.devtools.build.lib.packages.BuildType.LABEL_LIST;

import com.google.devtools.build.lib.analysis.BaseRuleClasses;
import com.google.devtools.build.lib.analysis.RuleDefinition;
import com.google.devtools.build.lib.analysis.RuleDefinitionEnvironment;
import com.google.devtools.build.lib.analysis.config.HostTransition;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.packages.Attribute;
import com.google.devtools.build.lib.packages.RuleClass;
import com.google.devtools.build.lib.packages.RuleClass.Builder;
import com.google.devtools.build.lib.util.FileType;

/**
 * Rule definition for the proto_library rule.
 */
public final class BazelProtoLibraryRule implements RuleDefinition {

  private static final Label DEFAULT_PROTO_COMPILER =
      Label.parseAbsoluteUnchecked("@com_google_protobuf//:protoc");
  private static final Attribute.LateBoundDefault<?, Label> PROTO_COMPILER =
      Attribute.LateBoundDefault.fromTargetConfiguration(
          ProtoConfiguration.class,
          DEFAULT_PROTO_COMPILER,
          (rule, attributes, protoConfig) ->
              protoConfig.protoCompiler() != null
                  ? protoConfig.protoCompiler()
                  : DEFAULT_PROTO_COMPILER);

  @Override
  public RuleClass build(Builder builder, final RuleDefinitionEnvironment env) {

    return builder
        .requiresConfigurationFragments(ProtoConfiguration.class)
        .setOutputToGenfiles()
        .add(attr(":proto_compiler", LABEL)
            .cfg(HostTransition.INSTANCE)
            .exec()
            .value(PROTO_COMPILER))
        /* <!-- #BLAZE_RULE(proto_library).ATTRIBUTE(deps) -->
        The list of other <code>proto_library</code> rules that the target depends upon.
        A <code>proto_library</code> may only depend on other
        <code>proto_library</code> targets.
        It may not depend on language-specific libraries.
        <!-- #END_BLAZE_RULE.ATTRIBUTE --> */
        .override(attr("deps", LABEL_LIST).allowedRuleClasses("proto_library").allowedFileTypes())
        /* <!-- #BLAZE_RULE(proto_library).ATTRIBUTE(srcs) -->
        The list of <code>.proto</code> and <code>.protodevel</code> files that are
        processed to create the target. This is usually a non empty list. One usecase
        where <code>srcs</code> can be empty is an <i>alias-library</i>. This is a
        proto_library rule having one or more other proto_library in <code>deps</code>.
        This pattern can be used to e.g. export a public api under a persistent name.
        <!-- #END_BLAZE_RULE.ATTRIBUTE --> */
        .add(
            attr("srcs", LABEL_LIST)
                .direct_compile_time_input()
                .allowedFileTypes(FileType.of(".proto"), FileType.of(".protodevel")))
        .advertiseProvider(ProtoSourcesProvider.class, ProtoSupportDataProvider.class)
        .build();
  }

  @Override
  public Metadata getMetadata() {
    return RuleDefinition.Metadata.builder()
        .name("proto_library")
        .ancestors(BaseRuleClasses.RuleBase.class)
        .factoryClass(BazelProtoLibrary.class)
        .build();
  }
}

/*<!-- #BLAZE_RULE (NAME = proto_library, TYPE = LIBRARY, FAMILY = Protocol Buffer) -->

<p>Use <code>proto_library</code> to define libraries of protocol buffers
   which may be used from multiple languages. A <code>proto_library</code> may be listed
   in the <code>deps</code> clause of supported rules, such as <code>java_proto_library</code>.
</p>

<p>When compiled on the command-line, a <code>proto_library</code> creates a file named
   <code>foo-descriptor-set.proto.bin</code>, which is the descriptor set for the
   messages the rule srcs. The file is a serialized <code>FileDescriptorSet</code>, which is
   described in
   <a href="https://developers.google.com/protocol-buffers/docs/techniques#self-description">
   https://developers.google.com/protocol-buffers/docs/techniques#self-description</a>.
</p>

<p>It only contains information about the <code>.proto</code> files directly mentioned by a
<code>proto_library</code> rule; the collection of transitive descriptor sets is available through
the <code>proto.transitive_descriptor_sets</code> Skylark provider.
See documentation in <code>ProtoSourcesProvider.java</code>.</p>

<!-- #END_BLAZE_RULE -->*/
