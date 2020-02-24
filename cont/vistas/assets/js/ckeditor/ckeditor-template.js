// Register a template definition set named "default".
CKEDITOR.addTemplates('default', {
    // The name of the subfolder that contains the preview images of the templates.
    imagesPath: CKEDITOR.getUrl(CKEDITOR.plugins.getPath('templates') + 'templates/images/'),

    // Template definitions.
    templates: [{
            title: 'Ejemplo template1',
            image: 'template1.gif',
            description: 'Description of My Template 1.',
            html: '<h2>Template 1</h2>' +
                '<p><img src="/logo.png" style="float:left" />Type your text here.</p>'
        },
        {
            title: '2 columnas',
            image: 'template1.gif',
            html: '<div class="row">' +
                '<div class="col-sm-6" style="background: #aaa;">aaa</div>' +
                '<div class="col-sm-6" style="background: #bbb;">bbb</div>' +
                '</div>'
        },
        {
            title: '4 columnas',
            image: 'template1.gif',
            html: '<div class="row">' +
                '<div class="col-sm-3" style="background: #aaa;">aaa</div>' +
                '<div class="col-sm-3" style="background: #bbb;">bbb</div>' +
                '<div class="col-sm-3" style="background: #ccc;">ccc</div>' +
                '<div class="col-sm-3" style="background: #ddd;">ddd</div>' +
                '</div>'
        }
    ]
});