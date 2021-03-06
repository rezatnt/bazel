// Copyright 2017 The Bazel Authors. All rights reserved.
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

package com.google.devtools.build.lib.analysis;

import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.config.BuildConfiguration;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.concurrent.ThreadSafety;
import com.google.devtools.build.lib.skyframe.serialization.ObjectCodec;
import com.google.devtools.build.lib.skyframe.serialization.autocodec.AutoCodec;
import com.google.devtools.build.lib.skylarkinterface.SkylarkCallable;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModule;
import com.google.devtools.build.lib.skylarkinterface.SkylarkModuleCategory;
import java.util.List;

/** A configuration fragment describing the current platform configuration. */
@AutoCodec
@ThreadSafety.Immutable
@SkylarkModule(
  name = "platform",
  doc = "The platform configuration.",
  category = SkylarkModuleCategory.CONFIGURATION_FRAGMENT
)
public class PlatformConfiguration extends BuildConfiguration.Fragment {
  public static final ObjectCodec<PlatformConfiguration> CODEC =
      new PlatformConfiguration_AutoCodec();

  private final Label executionPlatform;
  private final ImmutableList<Label> extraExecutionPlatforms;
  private final ImmutableList<Label> targetPlatforms;
  private final ImmutableList<Label> extraToolchains;
  private final ImmutableList<Label> enabledToolchainTypes;

  @AutoCodec.Instantiator
  PlatformConfiguration(
      Label executionPlatform,
      ImmutableList<Label> extraExecutionPlatforms,
      ImmutableList<Label> targetPlatforms,
      ImmutableList<Label> extraToolchains,
      ImmutableList<Label> enabledToolchainTypes) {
    this.executionPlatform = executionPlatform;
    this.extraExecutionPlatforms = extraExecutionPlatforms;
    this.targetPlatforms = targetPlatforms;
    this.extraToolchains = extraToolchains;
    this.enabledToolchainTypes = enabledToolchainTypes;
  }

  @SkylarkCallable(
    name = "execution_platform",
    structField = true,
    doc = "The current execution platform"
  )
  public Label getExecutionPlatform() {
    return executionPlatform;
  }

  /** Additional platforms that are available for action execution. */
  public ImmutableList<Label> getExtraExecutionPlatforms() {
    return extraExecutionPlatforms;
  }

  @SkylarkCallable(name = "platforms", structField = true, doc = "The current target platforms")
  public ImmutableList<Label> getTargetPlatforms() {
    return targetPlatforms;
  }

  /** Additional toolchains that should be considered during toolchain resolution. */
  public ImmutableList<Label> getExtraToolchains() {
    return extraToolchains;
  }

  @SkylarkCallable(
    name = "enabled_toolchain_types",
    structField = true,
    doc = "The set of toolchain types enabled for platform-based toolchain selection."
  )
  public List<Label> getEnabledToolchainTypes() {
    return enabledToolchainTypes;
  }
}
