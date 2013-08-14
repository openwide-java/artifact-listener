package fr.openwide.maven.artifact.notifier.web.application.common.component.navigation;

import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.primitives.Ints;

import fr.openwide.core.wicket.more.markup.html.navigation.paging.BootstrapPagingNavigation;
import fr.openwide.core.wicket.more.markup.html.navigation.paging.HideablePagingNavigator;
import fr.openwide.maven.artifact.notifier.web.application.navigation.util.LinkUtils;

public class HideableBookmarkablePagingNavigator extends HideablePagingNavigator {

	private static final long serialVersionUID = -7800388489760885324L;
	
	public static final String PAGE_NUMBER_PARAMETER = LinkUtils.PAGE_NUMBER_PARAMETER;
	
	public HideableBookmarkablePagingNavigator(String id, IPageable pageable, int viewSize) {
		super(id, pageable, viewSize);
	}

	public HideableBookmarkablePagingNavigator(String id, IPageable pageable, IPagingLabelProvider labelProvider,
			int viewSize) {
		super(id, pageable, labelProvider, viewSize);
	}

	public HideableBookmarkablePagingNavigator(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
		super(id, pageable, labelProvider);
	}

	public HideableBookmarkablePagingNavigator(String id, IPageable pageable) {
		super(id, pageable);
	}

	@Override
	protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
		PageParameters parameters = new PageParameters(getPage().getPageParameters());
		parameters.set(PAGE_NUMBER_PARAMETER, cullPageNumber(pageNumber));
		return new BookmarkablePageLink<Void>(id, getPage().getClass(), parameters);
	}
	
	@Override
	protected BootstrapPagingNavigation newNavigation(final String id, final IPageable pageable,
			final IPagingLabelProvider labelProvider) {
		return new BootstrapPagingNavigation(id, pageable, labelProvider) {
			private static final long serialVersionUID = 1L;

			@Override
			protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, long pageIndex) {
				return HideableBookmarkablePagingNavigator.this.newPagingNavigationLink(id, pageable, Ints.checkedCast(pageIndex));
			}
		};
	}

	private long cullPageNumber(int pageNumber) {
		long idx = pageNumber;
		if (idx < 0) {
			idx = getPageable().getPageCount() + idx;
		}

		if (idx > (getPageable().getPageCount() - 1)) {
			idx = getPageable().getPageCount() - 1;
		}

		if (idx < 0) {
			idx = 0;
		}

		return idx;
	}
}
