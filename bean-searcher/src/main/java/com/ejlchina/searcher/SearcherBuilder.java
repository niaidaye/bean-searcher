package com.ejlchina.searcher;

import com.ejlchina.searcher.dialect.MySqlDialect;
import com.ejlchina.searcher.implement.*;
import com.ejlchina.searcher.implement.convertor.DefaultFieldConvertor;
import com.ejlchina.searcher.implement.processor.DefaultParamProcessor;

/***
 * 检索器 Builder
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 * */
public class SearcherBuilder {

	/**
	 * 用于构建一个 BeanSearcher 实例
	 */
	public static BeanSearcherBuilder beanSearcher() {
		return new BeanSearcherBuilder();
	}

	/**
	 * 用于构建一个 MapSearcher 实例
	 */
	public static MapSearcherBuilder mapSearcher() {
		return new MapSearcherBuilder();
	}

	@SuppressWarnings("unchecked")
	static class DefaultSearcherBuilder<Builder extends DefaultSearcherBuilder<?>> {

		private SearchParamResolver searchParamResolver;

		private SqlResolver sqlResolver;

		private SqlExecutor sqlExecutor;

		private MetadataResolver metadataResolver;

		public Builder searchParamResolver(SearchParamResolver searchParamResolver) {
			this.searchParamResolver = searchParamResolver;
			return (Builder) this;
		}

		public Builder searchSqlResolver(SqlResolver sqlResolver) {
			this.sqlResolver = sqlResolver;
			return (Builder) this;
		}

		public Builder searchSqlExecutor(SqlExecutor sqlExecutor) {
			this.sqlExecutor = sqlExecutor;
			return (Builder) this;
		}

		public Builder metadataResolver(MetadataResolver metadataResolver) {
			this.metadataResolver = metadataResolver;
			return (Builder) this;
		}

		protected void buildInternal(AbstractSearcher mainSearcher) {
			if (searchParamResolver != null) {
				mainSearcher.setSearchParamResolver(searchParamResolver);
			}
			if (sqlResolver != null) {
				mainSearcher.setSearchSqlResolver(sqlResolver);
			} else {
				DefaultSqlResolver searchSqlResolver = new DefaultSqlResolver();
				searchSqlResolver.setDialect(new MySqlDialect());
				searchSqlResolver.setParamProcessor(new DefaultParamProcessor());
				mainSearcher.setSearchSqlResolver(searchSqlResolver);
			}
			if (sqlExecutor != null) {
				mainSearcher.setSearchSqlExecutor(sqlExecutor);
			} else {
				throw new SearcherException("你必须配置一个 searchSqlExecutor，才能建立一个检索器！ ");
			}
			if (metadataResolver != null) {
				mainSearcher.setMetadataResolver(metadataResolver);
			}
		}

	}


	public static class BeanSearcherBuilder extends DefaultSearcherBuilder<BeanSearcherBuilder> {

		private BeanReflector beanReflector;

		public BeanSearcherBuilder searchResultResolver(BeanReflector beanReflector) {
			this.beanReflector = beanReflector;
			return this;
		}

		public BeanSearcher build() {
			return build(new DefaultBeanSearcher());
		}

		public BeanSearcher build(DefaultBeanSearcher beanSearcher) {
			buildInternal(beanSearcher);
			if (beanReflector != null) {
				beanSearcher.setSearchResultResolver(beanReflector);
			} else {
				DefaultBeanReflector searchResultResolver = new DefaultBeanReflector();
				searchResultResolver.setFieldConvertor(new DefaultFieldConvertor());
				beanSearcher.setSearchResultResolver(searchResultResolver);
			}
			return beanSearcher;
		}

	}

	public static class MapSearcherBuilder extends DefaultSearcherBuilder<MapSearcherBuilder> {

		public MapSearcher build() {
			return build(new DefaultMapSearcher());
		}

		public MapSearcher build(DefaultMapSearcher beanSearcher) {
			buildInternal(beanSearcher);
			return beanSearcher;
		}

	}
	
}