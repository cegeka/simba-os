/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

(function($) {

	jQuery.fn.gallerax = function(options) {
		
		var settings = jQuery.extend({
			selectorParent:		jQuery(this)
		}, options);
		
		return jQuery.gallerax(settings);
	}

	jQuery.fn.galleraxFadeOut = function(speed, callback) {
		return this.fadeTo(speed, 0.0, callback);
	}

	jQuery.fn.galleraxFadeIn = function(speed, callback) {
		return this.fadeTo(speed, 1.0, callback);
	}

	jQuery.gallerax = function(options) {

		// Settings
		
			var settings = jQuery.extend({
			
				selectorParent:						null,						// If a jQuery object, all selectors will be restricted to its scope. Otherwise, all selectors will be global.
				
				// Selectors
				
					outputSelector:					null,						// 'Output' image selector
					outputImgSelector:				null,						// (scope: outputSelector) If null, assumes outputSelector points to the output IMG. Otherwise, we look for this child selector within outputSelector.
					captionSelector:				null,						// 'Caption' selector
					thumbnailsSelector:				null,						// 'Thumbnails' selector
					thumbnailsImgSelector:			null,						// (scope: thumbnailsSelector) If null, assumes thumbnailsSelector points to the thumbnail IMGs. Otherwise, we look for this child selector within thumbnailsSelector.
					navNextSelector:				null,						// 'Next' selector
					navPreviousSelector:			null,						// 'Previous' selector
					navFirstSelector:				null,						// 'First' selector
					navLastSelector:				null,						// 'Last' selector
					navStopAdvanceSelector:			null,						// 'Stop Advance' selector
					navPlayAdvanceSelector:			null,						// 'Play Advance' selector

				// General settings

					captionLines:					1,							// Number of caption lines. If this is > 1, the plugin will look for additional caption elements using the captionSelector setting + a number (eg. '.caption2', '.caption3', etc.)
					captionLineSeparator:			';',						// Character used to separate multiple caption lines within a thumbnail's 'title' attribute
					navWrap:						true,						// Wrap navigation when we navigate past the first or last thumbnail
					fade:							0,							// Fade duration for image transitions (0 for no fade, 'slow', 'fast', or a custom duration in ms)
					advanceDelay:					0,							// Time to wait (in ms) before automatically advancing to the next image (0 disables advancement entirely)
					advanceResume:					0,							// Time to wait (in ms) before resuming advancement after a user interrupts it by manually clicking on a thumbnail (0 disables resuming advancement)
					advanceFade:					0,							// Fade duration used for automatic image advancement (0 for no fade, 'slow', 'fast', or a custom duration in ms)
					advanceNavActiveClass:			'active',					// Active advancement navigation class
					thumbnailsActiveClass:			'active',					// Active thumbnail class
					thumbnailsFunction:				null,						// Function used to transform a thumbnail image's 'src' to the output image's 'src' (null indicates the thumbnail 'src' attributes can be used as is, ie. there are no separate full-size images for each thumbnail)
					thumbnailsPreloadOutput:		false						// If a thumbnailsFunction is defined, preload (ie. cache) each output image on page load

			}, options);
			
		// Variables

			// Operational stuff
		
				var isConfigured = true,
					isLocked = false,
					isAdvancing = false,
					cache = new Array(),
					list = new Array(),
					currentIndex = false,
					timeoutID;

			// jQuery objects

				var __output,
					__outputImg,
					__thumbnails,
					__caption,
					__navFirst,
					__navLast,
					__navNext,
					__navPrevious,
					__navStopAdvance,
					__navPlayAdvance;

		// Functions
			
			function cacheImage(url)
			{
				var x = document.createElement('img');
				x.src = url;
				cache.push(x);
			}
			
			function getElement(selector, required)
			{
				var x;
				
				try
				{
					if (selector == null)
						throw 'is undefined';
			
					if (settings.selectorParent)
						x = settings.selectorParent.find(selector);
					else
						x = jQuery(selector);
					
					if (x.length == 0)
						throw 'does not exist';
					
					return x;
				}
				catch (error)
				{
					if (required == true)
					{
						alert('Error: Required selector "' + selector + '" ' + error + '.');
						isConfigured = false;
					}
				}
				
				return null;
			}
			
			function advance()
			{
				if (settings.advanceDelay == 0)
					return;
			
				if (!isLocked)
					nextImage(settings.advanceFade);

				timeoutID = window.setTimeout(advance, settings.advanceDelay);
			}

			function initializeAdvance()
			{
				if (settings.advanceDelay == 0)
					return;

				if (__navPlayAdvance)
					__navPlayAdvance.addClass(settings.advanceNavActiveClass);
				
				if (__navStopAdvance)
					__navStopAdvance.removeClass(settings.advanceNavActiveClass);

				isAdvancing = true;
				timeoutID = window.setTimeout(advance, settings.advanceDelay);
			}
			
			function interruptAdvance()
			{
				if (!isAdvancing)
					return;

				if (settings.advanceDelay == 0)
					return;

				window.clearTimeout(timeoutID);

				if (settings.advanceResume == 0)
					return;

				timeoutID = window.setTimeout(advance, settings.advanceResume);
			}
			
			function stopAdvance()
			{
				if (settings.advanceDelay == 0)
					return;

				if (!isAdvancing)
					return;
			
				isAdvancing = false;
				window.clearTimeout(timeoutID);
			}
			
			function playAdvance(skip)
			{
				if (settings.advanceDelay == 0)
					return;

				if (isAdvancing)
					return;

				isAdvancing = true;

				if (skip)
					timeoutID = window.setTimeout(advance, settings.advanceDelay);
				else
					advance();
			}

			function firstImage()
			{
				switchImage(0);
			}
			
			function lastImage()
			{
				switchImage(list.length - 1);
			}

			function nextImage(speed)
			{
				if (currentIndex < list.length - 1)
					switchImage(currentIndex + 1, speed);
				else if (settings.navWrap)
					switchImage(0, speed);
			}
			
			function previousImage()
			{
				if (currentIndex > 0)
					switchImage(currentIndex - 1);
				else if (settings.navWrap)
					switchImage(list.length - 1);
			}

			function switchImage(index, speed)
			{
				// Check locking status (so another switch can't be initiated while another is in progress)

				if (isLocked)
					return false;
					
				isLocked = true;

				// Determine speed
			
					if (!speed)
						speed = settings.fade;
				
				// Get new and old thumbs
				
					var newThumb = list[index];
					var oldThumb = (currentIndex !== false ? list[currentIndex] : false);
				
				// Get new and old SRCs
				
					var newSRC = newThumb.fullUrl;
					var oldSRC = __outputImg.attr('src');
					
				// Initiate
				
					if (oldThumb)
						oldThumb.object.removeClass(settings.thumbnailsActiveClass);
					
					newThumb.object.addClass(settings.thumbnailsActiveClass);

				// Turn off old image
				
					__outputImg.galleraxFadeOut((oldThumb !== false ? speed : 0), function() {

						// Update caption

							if (__caption)
							{
								var i, s, caption;
							
								caption = unescape(newThumb.caption);
								
								if (settings.captionLines > 1)
								{
									var s = caption.split(settings.captionLineSeparator);
								
									for(i = 0; i < settings.captionLines; i++)
										__caption[i].html( (s[i] ? s[i] : '' ) );
								}
								else
									__caption.html(caption);
							}

						// Change image

							// Changing an image's "src" attribute doesn't seem to set its "complete"
							// attribute in IE to "false", even if the new image hasn't been cached yet.
							// However, cloning the original and using the clone in its place seems to
							// make things behave the way they should.
							if (jQuery.browser.msie)
							{
								var x = __outputImg.clone(true).attr('src', newSRC).insertAfter(__outputImg);
								__outputImg.remove();
								__outputImg = x;
							}
							else
							{
								__outputImg.attr('src', '');
								__outputImg.attr('src', newSRC);
							}
					
						// If the new output image has already been cached we can proceed immediately

							if (__outputImg.attr('complete'))
							{
								__outputImg.galleraxFadeIn(speed, function() {
									currentIndex = index;
									isLocked = false;
								});
							}

						// Otherwise, we need to do some other stuff ...

							else
							{
								var advanceState = isAdvancing;
								
								if (advanceState)
									stopAdvance();
							
								__outputImg.load(function() {
									__outputImg.galleraxFadeIn(speed, function() {
										currentIndex = index;
										isLocked = false;
									});
									
									if (advanceState)
										playAdvance();
									
									__outputImg.unbind('load');

								});
							}

					});
			}

			function initialize()
			{

				// Output

					__output = getElement(settings.outputSelector, true);
					__outputImg = (settings.outputImgSelector ? __output.find(settings.outputImgSelector) : __output);

				// Thumbnails

					__thumbnails = getElement(settings.thumbnailsSelector, true);

				// Captions

					if (settings.captionLines > 0)
					{
						if (settings.captionLines > 1)
						{
							var i, x;

							__caption = new Array();
							
							for(i = 0; i < settings.captionLines ; i++)
							{
								x = getElement(settings.captionSelector + (i > 0 ? (i + 1) : ''));
								
								if (x == null)
								{
									alert('Error: Option "captionLines" is set to "' + settings.captionLines + '", but I could not find a caption element for caption line ' + (i + 1) + '.');
									isConfigured = false;
									break;
								}
								
								__caption[i] = x;
							}
						}
						else
							__caption = getElement(settings.captionSelector);
					}
					
				// Navigation

					__navFirst = getElement(settings.navFirstSelector);
					__navLast = getElement(settings.navLastSelector);
					__navNext = getElement(settings.navNextSelector);
					__navPrevious = getElement(settings.navPreviousSelector);
					__navStopAdvance = getElement(settings.navStopAdvanceSelector);
					__navPlayAdvance = getElement(settings.navPlayAdvanceSelector);

				// Check configuration status
				
					if (isConfigured == false)
					{
						alert('Error: One or more configuration errors detected. Aborting.');
						return;
					}


				// Set up

					// Thumbnails
				
						__thumbnails.each(function(index) {
							var y = jQuery(this),
								yi = (settings.thumbnailsImgSelector ? y.find(settings.thumbnailsImgSelector) : y),
								thumbUrl = yi.attr('src'),
								fullUrl = thumbUrl;

							if (settings.thumbnailsFunction)
							{
								fullUrl = settings.thumbnailsFunction(yi.attr('src'));
								
								if (fullUrl != thumbUrl
								&&	settings.thumbnailsPreloadOutput)
									cacheImage(fullUrl);
							}
							
							list[index] = {
								object:		y,
								thumbUrl:	thumbUrl,
								fullUrl:	fullUrl,
								caption:	yi.attr('title')
							};
							
							y.click(function(event) {
								event.preventDefault();
								
								if (isLocked)
									return false;
								
								if (currentIndex != index)
								{
									interruptAdvance();
									switchImage(index);
								}
							});
							
							yi.hide();
							
							if (yi.get(0).complete)
								yi.galleraxFadeIn(settings.fade);
							else
								yi.load(function() {
									jQuery(this).galleraxFadeIn(settings.fade);
									jQuery(this).unbind('load');
								});
						});

					// Navigation

						if (__navNext)
							__navNext.click(function(event) {
								event.preventDefault();

								if (isLocked)
									return false;

								if (isAdvancing)
									interruptAdvance();
							
								nextImage();
							});

						if (__navPrevious)
							__navPrevious.click(function(event) {
								event.preventDefault();

								if (isLocked)
									return false;

								if (isAdvancing)
									interruptAdvance();
							
								previousImage();
							});

						if (__navFirst)
							__navFirst.click(function(event) {
								event.preventDefault();

								if (isLocked)
									return false;

								if (isAdvancing)
									interruptAdvance();
								
								firstImage();
							});

						if (__navLast)
							__navLast.click(function(event) {
								event.preventDefault();

								if (isLocked)
									return false;

								if (isAdvancing)
									interruptAdvance();

								lastImage();
							});

						if (__navStopAdvance)
							__navStopAdvance.click(function(event) {
								event.preventDefault();

								if (isLocked)
									return false;

								if (!isAdvancing)
									return false;

								__navStopAdvance.addClass(settings.advanceNavActiveClass);
								
								if (__navPlayAdvance)
									__navPlayAdvance.removeClass(settings.advanceNavActiveClass);

								stopAdvance();
							});

						if (__navPlayAdvance)
							__navPlayAdvance.click(function(event) {
								event.preventDefault();

								if (isLocked)
									return false;
									
								if (isAdvancing)
									return false;

								__navPlayAdvance.addClass(settings.advanceNavActiveClass);
								
								if (__navStopAdvance)
									__navStopAdvance.removeClass(settings.advanceNavActiveClass);

								playAdvance();
							});
			}

			// Ready

				jQuery().ready(function() {
					initialize();
					initializeAdvance();
					firstImage();
				});
	};

})(jQuery);