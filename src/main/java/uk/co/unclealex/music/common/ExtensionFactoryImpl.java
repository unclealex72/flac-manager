/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */

package uk.co.unclealex.music.common;

import com.google.common.base.Function;

/**
 * The default implementation of {@link ExtensionFactory}.
 * @author alex
 *
 */
public class ExtensionFactoryImpl implements ExtensionFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExtensionForMusicType(MusicType musicType) {
		class ExtensionFunction extends MusicTypeVisitor.Default implements Function<MusicType, String> {
			String extension;

			@Override
			public void visit(FlacType flacType) {
				extension = "flac";
			}

			@Override
			public void visit(OggType oggType) {
				extension = "ogg";
			}

			@Override
			public void visit(Mp3Type mp3Type) {
				extension = "mp3";
			}

			@Override
			public String apply(MusicType musicType) {
				musicType.accept(this);
				return extension;
			}
		}
		return new ExtensionFunction().apply(musicType);
	}

}
