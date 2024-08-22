import {registry} from '@jahia/ui-extender';
import QnAJson from './QnAJson';

import i18next from 'i18next';

i18next.loadNamespaces('game-4-jcustomer-components-quiz');

export default function () {
    registry.add('callback', 'QnAJsonEditor', {
        targets: ['jahiaApp-init:20'],
        callback: () => {
            registry.add('selectorType', 'QnAJson', {cmp: QnAJson, supportMultiple: false});
            console.debug('%c QnAJson Editor Extensions  is activated', 'color: #3c8cba');
        }
    });
}
