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
import com.google.common.base.Functions;

/**
 * The default implementation of {@link MusicTypeFactory}.
 * 
 * @author alex
 * 
 */
public class MusicTypeFactoryImpl implements MusicTypeFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MusicType createFlacType() {
		return new FlacTypeImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MusicType createOggType() {
		return new OggTypeImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MusicType createMp3Type() {
		return new Mp3TypeImpl();
	}

	class CompareFunction implements Function<MusicType, Class<? extends MusicType>> {

		@Override
		public Class<? extends MusicType> apply(MusicType musicType) {
			class Visitor extends MusicTypeVisitor.Default {
				Class<? extends MusicType> interfaceClass;

				@Override
				public void visit(FlacType flacType) {
					interfaceClass = FlacType.class;
				}

				@Override
				public void visit(OggType oggType) {
					interfaceClass = OggType.class;
				}

				@Override
				public void visit(Mp3Type mp3Type) {
					interfaceClass = Mp3Type.class;
				}
			}
			Visitor visitor = new Visitor();
			musicType.accept(visitor);
			return visitor.interfaceClass;
		}
	}

	abstract class AbstractMusicType<C extends MusicType> implements MusicType {

		private final Class<C> interfaceClass;

		public AbstractMusicType(Class<C> interfaceClass) {
			super();
			this.interfaceClass = interfaceClass;
		}

		@Override
		public int hashCode() {
			return getInterfaceClass().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof MusicType) && compareTo((MusicType) obj) == 0;
		}

		@Override
		public int compareTo(MusicType o) {
			Function<MusicType, String> f = Functions.compose(Functions.toStringFunction(), new CompareFunction());
			return f.apply(this).compareTo(f.apply(o));
		}

		@Override
		public String toString() {
			return getInterfaceClass().toString();
		}
		
		public Class<C> getInterfaceClass() {
			return interfaceClass;
		}

	}

	class FlacTypeImpl extends AbstractMusicType<FlacType> implements FlacType {

		public FlacTypeImpl() {
			super(FlacType.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(MusicTypeVisitor musicTypeVisitor) {
			musicTypeVisitor.visit((FlacType) this);
		}
	}

	class OggTypeImpl extends AbstractMusicType<OggType> implements OggType {

		public OggTypeImpl() {
			super(OggType.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(MusicTypeVisitor musicTypeVisitor) {
			musicTypeVisitor.visit((OggType) this);
		}
	}
	
	class Mp3TypeImpl extends AbstractMusicType<Mp3Type> implements Mp3Type {

		public Mp3TypeImpl() {
			super(Mp3Type.class);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void accept(MusicTypeVisitor musicTypeVisitor) {
			musicTypeVisitor.visit((Mp3Type) this);
		}
	}

}
